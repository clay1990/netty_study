package testReadCompeted;

import org.msgpack.annotation.Message;

/**
 * @Auther: yuyao
 * @Date: 2019/6/11 15:49
 * @Description:
 */
@Message
public class UserInfo   {
    private int id;
    private String name;
    private String sex;
    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}