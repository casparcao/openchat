# openchat

## 功能列表

1. 支持多种协议，tcp，websocket
2. 支持多类型终端，且可同时在线，且消息同步推送
3. 支持服务器的横向拓展，集群化部署，以满足不同并发场景
4. 面向开发者的聊天工具，代码的编写预览等功能，方便代码的分享，阅读
5. 支持文本文字，文件，图片分享
6. 基于SSL/TLS加密传输数据

## 所用技术&中间件

0. openjdk 15
1. netty 4.1.x
2. spring boot 2.3.x
3. webflux
4. redis
5. rabbitmq 3.19.x
6. redisson
7. mongodb
8. javafx
9. spring data reactive
10. protobuf3

## 模块

### openchat rest

- 基于spring webflux的restful接口
- 提供基于http协议的通信，主要用于登录获取token，拉取消息服务器ip
- 拉取好友关系
- 拉取消息列表

### openchat server

- 基于netty实现，服务器注册到redis，mongodb存储消息，rabbitmq进行消息推送
- 提供在线实时聊天功能
- 消息的离线存储
- 支持横向扩展

### openchat client

- 基于javafx实现的跨pc端客户端
- 双向证书认证
- 登录
- 聊天

### openchat core

- 基于protobuf3的序列化协议
- 通用的工具，实体类

## TODO

1. 注册
2. 加好友
3. 掉线重连
4. 群聊
5. 消息已读
6. 服务器注册
7. 支持emoji，图片，代码

## Done

1. 服务端的横向扩展能力
2. 在线消息的实时推送存储
3. 离线消息存储
