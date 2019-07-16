package com.clay.protocol;

import com.clay.constant.ConstantValue;

/**
 * @Auther: yuyao
 * @Date: 2019/5/28 14:06
 * @Description:
 */
public class SmartCarProtocol {

    private int head_data = ConstantValue.HEAD_DATA;

    private int contentLength;

    private byte[] content;

    public SmartCarProtocol(int contentLength, byte[] content) {
        this.contentLength = contentLength;
        this.content = content;
    }

    public int getHead_data() {
        return head_data;
    }

    public void setHead_data(int head_data) {
        this.head_data = head_data;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SmartCarProtocol{" +
                "head_data=" + head_data +
                ", contentLength=" + contentLength +
                ", content=" + new String(content) +
                '}';
    }
}