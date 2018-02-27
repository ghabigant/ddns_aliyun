# ddns_aliyun
100行代码搞定阿里云域名动态解析

###无聊写了个阿里ddns免去花生壳实名制的麻烦
###RT:
###1.必须要万网的域名
###2.配合隐性域名转发突破国内80和8080端口封锁
###3.借鉴了阿里云api源码,目前运用包和源码皆已经在官网下架,需要的自行在github查找,不提供全部源码
###4.使用了lambda,需要低版本运行环境自行修改源码
###5.配置文件格式,api key自行在阿里云官网寻找,功能免费,配置如下:
###文件名:ddns.properties
###内容:
###regionId = cn-shanghai
###accessKeyId = LTAIrEnv...
###secret = oCiIAvKLQ...
###domain = abigant.com
###sleepTime = 1
###
###使用细节不懂可发邮件:i@abigant.com
