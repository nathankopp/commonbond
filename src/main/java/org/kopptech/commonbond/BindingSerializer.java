package org.kopptech.commonbond;


public interface BindingSerializer
{
    String serialize(BindingNode root) throws Exception;
    void deserialize(BindingNode root, String value) throws Exception;
}
