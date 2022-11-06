package com.example;

public class kmp {
    class Solution {
        public int strStr(String haystack, String needle) {
            // haystack 原串    needle匹配串
            int h=haystack.length();
            int n=needle.length();
            for(int i=0;i<=h-n;i++){
                int a=0,b=i;
                while(a<n && haystack.charAt(b)==needle.charAt(a)){
                    a++;
                    b++;
                }
                if(a==n) return i;
            }
            return -1;
        }
    }
}
