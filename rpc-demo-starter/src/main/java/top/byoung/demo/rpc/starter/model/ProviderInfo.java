package top.byoung.demo.rpc.starter.model;

/**
 * @description: ProviderInfo
 * @author: Yang Weixin
 * @create: 2019/12/05
 */
public class ProviderInfo {
    private String name;

    private String addr;

    public ProviderInfo() {
    }

    public ProviderInfo(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public ProviderInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddr() {
        return addr;
    }

    public ProviderInfo setAddr(String addr) {
        this.addr = addr;
        return this;
    }

    @Override
    public String toString() {
        return "ProviderInfo{" +
            "name='" + name + '\'' +
            ", addr='" + addr + '\'' +
            '}';
    }
}
