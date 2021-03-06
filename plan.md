#### 基础框架的功能计划及BUG修改：
* 0、 ~~框架依赖的统一(去掉apache commons依赖)~~
* 1、 日志的分门别类存储(user_log、funding_log.....), 便于后期数据的使用(例如 : 统计...)
* 2、 RPC的功能完善(错误的显示) 调用超时的处理(基于RabbitMQ) 考虑使用DUBBO或者SpringCloud
* 3、 ~~框架底层的功能完善(针对一些公共的功能, 比如(CoreConfig)在不同环境采用不同的缓存处理策略等...)~~
* 4、 ~~getJSONFieldValue() 处理 ext.xxx = null的情况~~
* 5、 ~~修改TokenManager.generateUserToken 不能在这个方法中查询数据库~~
* 6、 以太坊平台账户转入以太币到用户账户改为异步(需要保证数据的正确性,如何保证???)
* 7、 以太坊监听链上交易重新设计 解决WebSocket不能接收到信息与超时断开
* 8、 接口返回code码的统一
* 9、 ~~验证实体可以通过配置常量开启(针对单个实体)~~
* 10、 ~~统一所有实体的status字段为实体字段 不再使用虚拟字段~~
* 11、 ~~同时更新虚拟字段和实体字段~~
* 12、 ~~搜索条件的动态组合  (比如：created_at>=start_at and created_at<=end_at)~~
* 13、 功能扩展点的重新设计 事件需要细化 新增异步事件机制 事件类的重新设计(CreateUserEvent、UpdateUserEvent....)
* 14、 ~~业务代码status的梳理 使用历史记录来记录数据的变化轨迹~~
* 15、 ~~数据字段的名字  下划线还是驼峰命名???~~
* 16、 实体(模型)与资源绑定的重新实现 (自动更新、删除旧的、保留旧的...)
* 17、 ~~CMS可以通过category_id查询子类的文章列表~~
* 19、 数据库的主键ID是否使用分布式ID???
* 20、 ~~验证码验证抽取为注解实现 减少原本代码的数量 利于重复使用~~
* 21、 ~~数据输出(结果集)过滤~~
* 22、 ~~changeEntityJSONFieldXXXX()函数可以增减字段~~
* 23、 RabbitMQ实现的RPC能够支持分布式  部分功能的重写
* 24、 ~~RabbitMQ实现的RPC调用能够配置是否启用~~
* 25、 ~~JsonToMapTypeHandler::jsonToMap()与JsonToListTypeHandler::jsonToList()需要修改(属性需要从缓存中读取)
       ,防止拖慢数据库~~
* 26、 ~~Quartz的使用可以配置~~
* 27、 ~~级联查询在统计分页的时候要禁止其一起去查关联的数据~~
* 28、 ~~分页工具类的BUG修复~~
* 29、 ~~新增Activiti工作流引擎~~
* 30、 SSO单点登录
* 31、 ~~EMail的发送~~
* 32、 预防CSRF攻击
* 33、 预防XSS攻击
* 34、 代码生成器
* 35、 RPC需要检测被调用的服务是否存在
* 36、 ~~在处理请求中的附加属性时,将附加属性中包含的字段填充上相应的默认值(比如 : ext:{"name":"britton","email":"britton@126.com"})~~
* 37、 属性分组需要有层级关系
* 38、 模型新增的字段在查询旧数据时需要将新增字段一并显示出来
* 39、 模型字段的删减触发相应的事件，便于后期加入统计报表功能,注意:如果需要直接在数据库中修改,可以配合canal使用
* 40、 整合MongoDB，便于MySQL与MongoDB的切换与混合使用
* 41、 core_model_attributes数据表新增字段(entity_field_name),该字段用来表示该行数据在实体表中的实际字段,新增该字段之后,需要将attribute_id关联到core_attribute数据表上
* 42、 core_attributes表中的validate_desc字段修改为error_tips, core_attributes表中的validate_expression修改为validate_rule
* 43、 core_attributes_enums中新增字段(frontend_input_type),表示枚举类型的前端显示类型(下拉、多选、单选....),并且新增与core_attributes的外键约束
* 44、 删除core_model、core_media_library表中与PHP相关的字段
* 45、 核心功能的重构,将解析json参数到对应entity的功能抽离出来
* 46、 核心功能重构,将框架本身的核心功能抽离出来做成独立的starter
* 47、 core_model_attributes_permission中数据的存储在缓存中的存储格式为: 角色Id:{"allow":[1,2,3,4,5] , "deny":[7,8,9,10]} , 用户Id:{"allow":[4,5,6],"deny":[1,2,9]},如果两者同时存在,最终以用户Id包含的项为准.
* 51、 弃用RabbitMQ实现的RPC


* 80、 动态根据数据库配置生成实体文件
* 81、 代码的安全审计
* 82、 源代码的加解密
* 83、 动态报表开发


* 100、 账户子帐号
* 101、 分销(佣金计算...)
