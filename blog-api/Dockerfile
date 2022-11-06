FROM java:8
EXPOSE 8889
ADD ./blog_admin.jar /admin.jar
RUN bash -c 'touch /admin.jar'
ENTRYPOINT ["java","-jar","/admin.jar","--spring.profiles.active=prod"]
