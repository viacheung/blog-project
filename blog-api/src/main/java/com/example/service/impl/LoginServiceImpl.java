package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.domain.SysUser;
import com.example.service.LoginService;
import com.example.service.SysUserService;
import com.example.utils.JWTUtils;
import com.example.vo.ErrorCode;
import com.example.vo.Result;
import com.example.vo.params.LoginParam;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.vo.ErrorCode.ACCOUNT_PWD_NOT_EXIST;
/*1．检查参数是否合法
        2．根据用户名和密码去user表中查询是否存在
        3.如果不存在登录失败
        4.如果存在，使用jwt生成token返回给前端
        5. token放入redis当中，,redis token: user信息、设置过期时间（登录认证的时候先认证token字符串是否合法，去redis认证是否存在)*/
@Service
public class LoginServiceImpl implements LoginService {
    private static final String slat = "mszlu!@#";
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private SysUserService sysUserService;
    @Override
    public Result login(LoginParam loginParam) {
        String account=loginParam.getAccount();
        String password=loginParam.getPassword();
        //如果输入为空

        if(StringUtils.isBlank(account)||StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        //对密码进行处理的原因是数据库sql的密码也是经过加密的
        //注意 这里要导commons-codec的类
        String pwd= DigestUtils.md5Hex(password+slat);
        SysUser sysUser=sysUserService.findUser(account,pwd);
        //用户不存在
        if(sysUser==null){
            return Result.fail(ACCOUNT_PWD_NOT_EXIST.getCode(), ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        String token=JWTUtils.createToken(sysUser.getId());
        //opsForValue()为了获得各种api set设置键值对，JSON.toJSONString是阿里巴巴的fastjson，把类转为json，后面1是过期时间，后面是单位
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }
/*1．判断参数是否合法
2．判断账户是否存在，存在返回账户已经被注册3，不存在，注册用户
4、生成token
5．存入redis 并返回
6．注意加上事务，一旦中间的任何过程出现问题，注册的用户需要回滚*/
    @Override
    public Result register(LoginParam loginParam) {
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if(StringUtils.isBlank(account)||StringUtils.isBlank(password)||StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        SysUser sysUser=sysUserService.findUserByAccount(account);
        //已经存在该账户
        if(sysUser!=null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
        }
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));//和登录一致 也用加密盐
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        sysUserService.save(sysUser);//存到数据库里面
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token,JSON.toJSONString(sysUser),1,TimeUnit.DAYS);
        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {
        //我写的 直接从redis拿
/*        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;*/
        //up主写的 判断了是否为空
        if (StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        //防止空指针
        if (stringObjectMap == null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);

        //空数据直接返回空
        if (StringUtils.isBlank(userJson)){
            return null;
        }
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }

}
