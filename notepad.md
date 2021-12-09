## 证书生成

- 生成服务器端JKS
```shell script
keytool -genkeypair -alias top.mikecao.pchat.server -keypass top.mikecao.pchat.server -storepass top.mikecao.pchat.server -dname "C=CN,ST=GD,L=SZ,O=mikecao.top,OU=dev,CN=mikecao.top" -keyalg RSA -keysize 2048 -validity 3650 -keystore top.mikecao.pchat.server.keystore
```
- 从JKS中导出证书
```shell script
keytool -exportcert -keystore top.mikecao.pchat.server.keystore -file top.mikecao.pchat.server.cer -alias top.mikecao.pchat.server -storepass top.mikecao.pchat.server
```
- 生成客户端JKS
```shell script
keytool -genkeypair -alias top.mikecao.pchat.pc -keypass top.mikecao.pchat.pc -storepass top.mikecao.pchat.pc -dname "C=CN,ST=GD,L=SZ,O=mikecao.top,OU=dev,CN=mikecao.top" -keyalg RSA -keysize 2048 -validity 3650 -keystore top.mikecao.pchat.pc.keystore
```
- 从JKS中导出证书
```shell script
keytool -exportcert -keystore top.mikecao.pchat.pc.keystore -file top.mikecao.pchat.pc.cer -alias top.mikecao.pchat.pc -storepass top.mikecao.pchat.pc
```
- 将服务器端证书导入客户端JKS
```shell script
keytool -importcert -keystore top.mikecao.pchat.pc.keystore -file top.mikecao.pchat.server.cer -alias top.mikecao.pchat.server -storepass top.mikecao.pchat.pc -noprompt
```
- 将客户端证书导入服务器端JKS
```shell script
keytool -importcert -keystore top.mikecao.pchat.server.keystore -file top.mikecao.pchat.pc.cer -alias top.mikecao.pchat.pc -storepass top.mikecao.pchat.server -noprompt
```
