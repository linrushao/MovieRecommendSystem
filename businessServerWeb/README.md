## 工程简介
基于实时和离线的电影推荐系统
## 涉及技术
###1 spark
sparkSQL用于离线计算，也就是直接按需求查库
sparkStream 用于实时推荐，利用流的方式，将传送过来的数据进行处理
####1.1 实时计算
因为实时计算是需要有数据流过来，然后才能实时计算，也就是说，在实时计算的时候，我们需要提前把主要的计算模型创建好。
####1.2 流推荐
在数据传输过来的时候，只需要把数据传入到我们的定义好的模型中就可以计算出相应的推荐结果
###2 mongodb
####使用mongodb的原因
#####2.1 存储数据，因为电影数据信息比较复杂，属于半结构化数据，使用MySQL数据库的时候设计表不方便，特别是在实时推荐的时候需要已矩阵的形式推荐，也就是说在向一个用户推荐电影的时候，需要推荐的不只是一个电影而是有多个，虽然在MySQL中使用外键关联表也是可以的，但是比较麻烦
#####2.2 主要的原因还是spark的功能，在实时推荐的时候，尝试利用hive作为我们的数据库，但是在实时计算时，使用sparksession算子的时候发现，此算子不支持在调用方法时传入，这不符合spark设计的初衷，在网上找了需要的解决方法，发现在我这里都是行不通的，所以在此时，我便更换了数据库，改为使用mongdb数据库
###3 hive
这个主要是熟悉一下(hive in spark)在此项目中只是练习只是想练习一下如何将spark数据存在hive中
###4 redis 
这个主要是用于缓存作用，在实时作用中发挥作用
###5 kafka
kafka一般与spark实时计算结合在一起使用，Kafka是一个消息队列，可以先将数据存放在缓存中，等我们需要的时候直接拿来使用即可
###6 elasticsearch
这个组件主要用于电影推荐系统的查询功能
###7 flume
flume组件主要是用于日志的采集，就是需要我们实时采集用户的行为状态，实时的将数据传送到Kafka中，spark再从Kafka消息队列中取出数据进行实时的计算推荐
###8 spring boot和thymeleaf
主要是用于前端页面的展示

##spring boot主要的步骤
###1 创建实体类
###2 mapper层（持久层）
用于sql语句的实现，因为我们没有使用MySQL和hive，所以此层便没有写
###3 service层（业务层）
主要编写业务逻辑代码，将再数据库中查到的需要的数据进行封装，等待使用
###4 controller层（控制层）
主要与前端建立连接，注入service层。将在service层封装好的数据，传输到前端


##登录逻辑
先展示首页，其他功能都不能使用，使用拦截器将所有的页面全部拦截
除了：首页（一运行就是首页推荐页面，也就是冷启动问题），登录页面，注册页面，所有的样式
登录成功后，将开放所有的功能，并对登录的用户进行离线推荐的页面展示出来，实时推荐的根据以往的类别啥的先随机推荐出来，等待用户使用后再精确推荐

##前端
###1 整体框架的实现
####1.1 common 
这是前端框架中的公有组件，主要是上组件的，包括logo、搜索框、登录、注销功能
所有的前端页面都继承此组件
右导航栏：主要是前端的功能选择，比如首页显示，实时推荐按钮，离线推荐按钮等
脚部：我没有设置footer，因为现在我只知道footer是显示一些公司的信息和我们想要显示的信息而已，因为这是我的练手项目，所以忽略，最后完成项目后可以做优化
####1.2movieGenres
电影类别：公有二十个类别的页面，统一放在此文件夹中，方便后续管理
####1.3其他
其他的页面在边做便写


##各文件夹的作用
###1 config（配置文件）
####1.1 elasticSearchConfig
用于elasticsearch与springboot集成连接的作用，虽然在springboot中有有elasticSearch集成组件，但是在使用的时候出现了很多问题，版本的问题最多
在连接的时候一直报错，网上查了基本都是说版本的兼容问题，而elasticSearch我是第一次使用的，对此版本的了解并不是很多，所以在出现问题试了很多的解决方法都没有成功。
虽然知道了是版本兼容问题，但是还是无法解决，最后只能依赖于mavenjar包重新导入，不集成原来springboot原有的elasticSearch支持。
版本兼容问题：
- 原来的集成接口，tran连接的接口在版本6.x中是支持的，在7.x的时候过时了，在8.x时候会被弃用
```xml
<dependency>
   <groupId>org.elasticsearch.client</groupId>
   <artifactId>transport</artifactId>
   <version>7.15.2</version>
</dependency>
```
这是原来一直使用的接口，在更改版本后和解决一些其他的依赖问题后还是不能用

- 现在使用的elasticsearch-rest-high-level-client，现在使用三个依赖，就是elasticSearch高级接口
```xml
<dependency>
    <dependency>
        <groupId>org.elasticsearch</groupId>
        <artifactId>elasticsearch</artifactId>
        <version>7.6.2</version>
    </dependency>
    <dependency> 
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-high-level-client</artifactId>
        <version>7.6.2</version>
    </dependency> 
    <dependency>
        <groupId>org.elasticsearch.client</groupId> 
        <artifactId>elasticsearch-rest-client</artifactId>
        <version>7.6.2</version>
    </dependency>
</dependency>
```
ES版本与SpringBoot中的Spring Data Elasticsearch没有合适的版本，在项目启动时给出警告提示。于是决定放弃使用Spring Data Elasticsearch，直接使用elasticsearch-rest-high-level-client来操作ES

####1.2 LoginInterceptorConfigurer（登录拦截器）
- 作用：一个网站，在没有登录的时候，应该只能看到首页、登录页面、注册页面，按照需求添加在没有登录时需要显示的页面。没有登录的时候，在点击其他页面时或者在前端直接输入/user.html，这些都应该需要被拦截的，拦截时直接跳转到登录页面即可，在登录成功后恢复所有的功能。在拦截时，因为页面需要样式显示，图片，js等的这些统统都要放行的。然后其他的一些都拦截
- 具体：简单来说就是两个名单：黑名单和白名单
- 白名单：就是需要放行的页面的或样式
- 黑名单：除了放行的页面或样式，其他一切都拦截，在页面拦截的时候实际就是默认跳转到登录页面。
```java
 @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 创建拦截器对象
        HandlerInterceptor interceptor = new LoginInterceptor();

        // 白名单
        List<String> patterns = new ArrayList<String>();
        patterns.add("/css/**");
        patterns.add("/fonts/**");
        patterns.add("/images/**");
        patterns.add("/js/**");
        patterns.add("/loginbootstrap/**");
        patterns.add("/register");
        patterns.add("/user/register");
        patterns.add("/login");
        patterns.add("/user/login");
        patterns.add("/register.html");
        patterns.add("/login.html");
        patterns.add("/");
        patterns.add("/index.html");
        patterns.add("/common/common.html");

        // 通过注册工具添加拦截器
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(patterns);
    }
```
####1.3 图片的路径
- 图片原本是放在static目录下的，此目录需要跟随项目的编译，如果图片少可以直接放在当前目录下，但是我们制作的是电影推荐系统，所以图片是必然不会少的，一个庞大的图片集如果直接放在static目录下，虽然可以正常编译执行，但是在编译时会非常吗，所以是不提倡直接放在static目录文件下的。此时可以搞个文件路径映射
```java
   @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //上传的图片在D盘下的img目录下，访问路径如：http://localhost:8081/image/1.jpg
        //其中image表示访问的前缀。"file:D:/img/"是文件真实的存储路径
        registry.addResourceHandler("/images/**").addResourceLocations("file:D:/movieSystemImages/images/");

    }
```
在真实的项目中，一般都会有一个图片服务器的，就是专门存放图片的地方，而前端访问图片时，是根据后端的路径找到该图片的，所以后端只需要把图片的地址传送正确，前端就能正常访问。在java中，可以利用映射关系，将我们真实的地址映射在前端中，就相当于把我们的原来存图片的路径映射在了static中，这样也是可以正常访问的。
- 为什么不在数据库中存放真实的图片： 
如果在数据库中存放真实的图片，数据库会把图片先以二进制的形式存入到数据库中，取出来时也是通过解二进制码形成图片再显示的，所以这个过程会比较消耗性能，一般不会这样做。






