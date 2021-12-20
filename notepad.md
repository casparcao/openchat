## 证书生成

- 生成服务器端JKS
```shell script
keytool -genkeypair -alias top.mikecao.openchat.server -keypass top.mikecao.openchat.server -storepass top.mikecao.openchat.server -dname "C=CN,ST=GD,L=SZ,O=mikecao.top,OU=dev,CN=mikecao.top" -keyalg RSA -keysize 2048 -validity 3650 -keystore top.mikecao.openchat.server.keystore
```
- 从JKS中导出证书
```shell script
keytool -exportcert -keystore top.mikecao.openchat.server.keystore -file top.mikecao.openchat.server.cer -alias top.mikecao.openchat.server -storepass top.mikecao.openchat.server
```
- 生成客户端JKS
```shell script
keytool -genkeypair -alias top.mikecao.openchat.pc -keypass top.mikecao.openchat.pc -storepass top.mikecao.openchat.pc -dname "C=CN,ST=GD,L=SZ,O=mikecao.top,OU=dev,CN=mikecao.top" -keyalg RSA -keysize 2048 -validity 3650 -keystore top.mikecao.openchat.pc.keystore
```
- 从JKS中导出证书
```shell script
keytool -exportcert -keystore top.mikecao.openchat.pc.keystore -file top.mikecao.openchat.pc.cer -alias top.mikecao.openchat.pc -storepass top.mikecao.openchat.pc
```
- 将服务器端证书导入客户端JKS
```shell script
keytool -importcert -keystore top.mikecao.openchat.pc.keystore -file top.mikecao.openchat.server.cer -alias top.mikecao.openchat.server -storepass top.mikecao.openchat.pc -noprompt
```
- 将客户端证书导入服务器端JKS
```shell script
keytool -importcert -keystore top.mikecao.openchat.server.keystore -file top.mikecao.openchat.pc.cer -alias top.mikecao.openchat.pc -storepass top.mikecao.openchat.server -noprompt
```
