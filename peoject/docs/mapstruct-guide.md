# MapStruct 使用总结

MapStruct 用来在 Java Bean、Entity、DTO、VO 之间做字段映射。它不是运行时反射工具，而是在编译期通过注解处理器生成实现类，所以性能接近手写代码。

在本项目中，典型场景是把 `SysUser` 转成登录接口返回对象 `LoginRes`。

## Maven 依赖

基础依赖需要包含 MapStruct 本体和 MapStruct 注解处理器：

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.5.Final</version>
</dependency>

<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.5.Final</version>
    <scope>provided</scope>
</dependency>
```

如果项目同时使用 Lombok，建议保留 Lombok，并在注解处理器里额外配置 `lombok-mapstruct-binding`，避免 MapStruct 在 Lombok 生成 getter、setter、builder 之前读取不到属性。

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

`lombok-mapstruct-binding` 一般只需要放在 `annotationProcessorPaths` 中，不一定要放到普通 dependencies 里。

## annotationProcessorPaths 配置

如果 `maven-compiler-plugin` 中显式配置了 `annotationProcessorPaths`，Maven 只会使用这里列出的注解处理器。此时即使 dependencies 中有 `mapstruct-processor`，如果这里漏配，MapStruct 也不会生成 `MapperImpl`。

推荐配置：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </path>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.5.5.Final</version>
            </path>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok-mapstruct-binding</artifactId>
                <version>0.2.0</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

如果测试代码里也写了 MapStruct Mapper，需要在 `testCompile` 对应的注解处理器配置中也加入 MapStruct。

编译后可以在下面目录查看生成类：

```text
target/generated-sources/annotations
```

例如：

```text
target/generated-sources/annotations/com/example/flowdesk/security/mapstruct/LoginResStructMapperImpl.java
```

## 方案一：交给 Spring 管理

Spring Boot 项目中推荐使用这种方式。

Mapper 接口：

```java
package com.example.flowdesk.security.mapstruct;

import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.system.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoginResStructMapper {

    @Mapping(source = "id", target = "userId")
    LoginRes toLoginRes(SysUser sysUser);
}
```

Service 中通过构造器注入使用：

```java
package com.example.flowdesk.security.service.impl;

import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.security.mapstruct.LoginResStructMapper;
import com.example.flowdesk.system.entity.SysUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final LoginResStructMapper loginResStructMapper;

    public LoginRes login() {
        SysUser sysUser = new SysUser();
        return loginResStructMapper.toLoginRes(sysUser);
    }
}
```

这种方式下，MapStruct 生成的 `LoginResStructMapperImpl` 会被注册成 Spring Bean，Spring 才能注入它。

## 方案二：手动获取 Mapper 实例

如果不想交给 Spring 管理，可以使用 MapStruct 自带的 `Mappers.getMapper(...)`。

Mapper 接口：

```java
package com.example.flowdesk.security.mapstruct;

import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.system.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoginResStructMapper {

    LoginResStructMapper INSTANCE = Mappers.getMapper(LoginResStructMapper.class);

    @Mapping(source = "id", target = "userId")
    LoginRes toLoginRes(SysUser sysUser);
}
```

Service 中不要再声明 `private final LoginResStructMapper`，直接通过 `INSTANCE` 调用：

```java
package com.example.flowdesk.security.service.impl;

import com.example.flowdesk.security.dto.res.LoginRes;
import com.example.flowdesk.security.mapstruct.LoginResStructMapper;
import com.example.flowdesk.system.entity.SysUser;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {

    public LoginRes login() {
        SysUser sysUser = new SysUser();
        return LoginResStructMapper.INSTANCE.toLoginRes(sysUser);
    }
}
```

这种方式下，`LoginResStructMapperImpl` 只是普通 Java 对象，不是 Spring Bean。

## 两种方案不能混用

下面这种写法会报错：

```java
@Mapper
public interface LoginResStructMapper {
    LoginResStructMapper INSTANCE = Mappers.getMapper(LoginResStructMapper.class);
}
```

同时又在 Service 中写：

```java
private final LoginResStructMapper loginResStructMapper;
```

原因是普通 `@Mapper` 不会把生成类注册为 Spring Bean，但 `private final` 字段配合 `@RequiredArgsConstructor` 会让 Spring 尝试从容器里注入这个 Bean。Spring 找不到，就会报类似错误：

```text
Parameter 1 of constructor in ...AuthServiceImpl required a bean of type
'...LoginResStructMapper' that could not be found.
```

正确做法是二选一：

- 使用 `@Mapper(componentModel = "spring")` 时，通过 Spring 注入调用。
- 使用 `@Mapper` + `Mappers.getMapper(...)` 时，不要让 Spring 注入，直接使用 `INSTANCE`。

## 常见问题

### 找不到 LoginResStructMapper Bean

常见原因：

- Mapper 写的是 `@Mapper`，没有写 `componentModel = "spring"`。
- Service 中仍然使用构造器注入 `LoginResStructMapper`。
- MapStruct 没有生成实现类。

解决方式：

- Spring 项目推荐改成 `@Mapper(componentModel = "spring")`。
- 或者删除 Service 中的 Mapper 注入，改用 `LoginResStructMapper.INSTANCE`。

### 没有生成 MapperImpl

常见原因：

- `annotationProcessorPaths` 中漏了 `mapstruct-processor`。
- Maven 没有重新编译。
- IDE 关闭了 annotation processing。

解决方式：

```text
mvn clean compile
```

然后检查：

```text
target/generated-sources/annotations
```

### 字段名不一样怎么映射

字段名一致时，MapStruct 会自动映射。字段名不一致时，用 `@Mapping` 指定：

```java
@Mapping(source = "id", target = "userId")
LoginRes toLoginRes(SysUser sysUser);
```

这里表示把 `SysUser.id` 映射到 `LoginRes.userId`。


## 常用能力和注解

### 多个字段映射

多个字段名不一致时，可以写多个 `@Mapping`：

```java
@Mapper(componentModel = "spring")
public interface LoginResStructMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "departmentId", target = "deptId")
    LoginRes toLoginRes(SysUser sysUser);
}
```

### 忽略字段

目标对象里某个字段不想映射，可以使用 `ignore = true`：

```java
@Mapping(source = "id", target = "userId")
@Mapping(target = "token", ignore = true)
LoginRes toLoginRes(SysUser sysUser);
```

### 常量和默认值

固定写入一个值，用 `constant`：

```java
@Mapping(target = "loginType", constant = "PASSWORD")
LoginRes toLoginRes(SysUser sysUser);
```

源字段为 `null` 时使用默认值，用 `defaultValue`：

```java
@Mapping(source = "nickname", target = "nickname", defaultValue = "未设置昵称")
LoginRes toLoginRes(SysUser sysUser);
```

### 表达式映射

需要简单计算时，可以使用 `expression`：

```java
@Mapping(target = "displayName", expression = "java(sysUser.getUsername() + \"(\" + sysUser.getNickname() + \")\")")
LoginRes toLoginRes(SysUser sysUser);
```

表达式会直接写进生成代码里，复杂逻辑不建议放这里，最好抽成方法。

### 自定义转换方法

如果某个字段需要特殊转换，可以在 Mapper 中写默认方法，MapStruct 会自动调用匹配的方法：

```java
@Mapper(componentModel = "spring")
public interface LoginResStructMapper {

    @Mapping(source = "status", target = "enabled")
    LoginRes toLoginRes(SysUser sysUser);

    default Boolean statusToEnabled(String status) {
        return "1".equals(status);
    }
}
```

### 集合映射

单个对象能映射时，集合通常也可以直接映射：

```java
@Mapper(componentModel = "spring")
public interface LoginResStructMapper {

    @Mapping(source = "id", target = "userId")
    LoginRes toLoginRes(SysUser sysUser);

    List<LoginRes> toLoginResList(List<SysUser> sysUsers);
}
```

需要导入：

```java
import java.util.List;
```

### 更新已有对象

如果不是新建 DTO，而是把源对象的值更新到已有对象中，可以使用 `@MappingTarget`：

```java
@Mapper(componentModel = "spring")
public interface SysUserStructMapper {

    void updateSysUser(UserUpdateReq req, @MappingTarget SysUser sysUser);
}
```

这种写法常用于“根据请求对象更新数据库实体”，可以避免手写大量 setter。

### 多个入参映射到一个对象

MapStruct 支持从多个对象或参数中取值：

```java
@Mapper(componentModel = "spring")
public interface LoginResStructMapper {

    @Mapping(source = "sysUser.id", target = "userId")
    @Mapping(source = "sysUser.username", target = "username")
    @Mapping(source = "token", target = "token")
    LoginRes toLoginRes(SysUser sysUser, String token);
}
```

多个入参时，`source` 建议写完整路径，例如 `sysUser.id`，避免字段来源不清楚。

### 反向映射

如果两个对象之间需要双向转换，可以使用 `@InheritInverseConfiguration` 复用反向配置：

```java
@Mapper(componentModel = "spring")
public interface UserStructMapper {

    @Mapping(source = "id", target = "userId")
    LoginRes toLoginRes(SysUser sysUser);

    @InheritInverseConfiguration
    SysUser toSysUser(LoginRes loginRes);
}
```

需要导入：

```java
import org.mapstruct.InheritInverseConfiguration;
```

### 引用其他 Mapper

当对象里有嵌套对象，或者不同模块有各自的 Mapper，可以用 `uses` 引用：

```java
@Mapper(componentModel = "spring", uses = {DepartmentStructMapper.class})
public interface LoginResStructMapper {

    LoginRes toLoginRes(SysUser sysUser);
}
```

使用 `componentModel = "spring"` 时，被 `uses` 引用的 Mapper 通常也建议配置成 Spring Bean。

### 空值处理

更新对象时，如果源字段为 `null`，希望不要覆盖目标对象原来的值，可以配置：

```java
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SysUserStructMapper {

    void updateSysUser(UserUpdateReq req, @MappingTarget SysUser sysUser);
}
```

需要导入：

```java
import org.mapstruct.NullValuePropertyMappingStrategy;
```

这种配置适合 PATCH 或局部更新场景。