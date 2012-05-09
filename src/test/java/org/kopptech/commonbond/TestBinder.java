package org.kopptech.commonbond;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.kopptech.commonbond.Binder;
import org.kopptech.commonbond.BindingNode;
import org.kopptech.commonbond.InputStyle;
import org.kopptech.commonbond.ObjUtils;
import org.kopptech.commonbond.bindings.ListBinding;
import org.kopptech.commonbond.json.JsonBindingReader;
import org.kopptech.commonbond.json.JsonBindingSerializer;


public class TestBinder
{
    @Test
    public void testBinder() throws Exception
    {
        //BindingNode bind = setupBasicBind(createBasicObj());
        BindingNode bind = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("basic-binding.txt"), createBasicObj());
        
        Assert.assertEquals(4, bind.getChildren().size());
        Assert.assertEquals(null,bind.getTagName());
        
        BindingNode val1 = bind.getChildByTagName("val1t");
        Assert.assertTrue(val1 == bind.getChildByFieldName("val1"));
        Assert.assertEquals("abc", val1.getValue());
        BindingNode val2 = bind.getChildByTagName("val2t");
        Assert.assertTrue(val2 == bind.getChildByFieldName("val2"));
        Assert.assertEquals("def", val2.getValue());
        
        BindingNode obj1 = bind.getChildByTagName("obj1t");
        Assert.assertTrue(obj1 == bind.getChildByFieldName("obj1"));
        ChildObj obj1Obj = (ChildObj)obj1.getValue();
        Assert.assertEquals(123, obj1Obj.getX());
        Assert.assertTrue(obj1.getChildByTagName("xt") == obj1.getChildByFieldName("x"));
        Assert.assertEquals(123, obj1.getChildByFieldName("x").getValue());
        Assert.assertEquals(456, obj1.getChildByFieldName("y").getValue());
        
        BindingNode list1 = bind.getChildByTagName("list1t");
        Assert.assertTrue(list1 == bind.getChildByFieldName("list1"));
        ((ListBinding)list1.getBinding()).setCurrentIdx(0);
        ChildObj list1Obj = (ChildObj)list1.getValue();
        Assert.assertEquals("xyz", list1Obj.getX());
        Assert.assertTrue(list1.getChildByTagName("xt") == list1.getChildByFieldName("x"));
        Assert.assertEquals("xyz", list1.getChildByFieldName("x").getValue());
        Assert.assertEquals("pdq", list1.getChildByFieldName("y").getValue());
        
    }

    @Test
    public void testJsonExport() throws Exception
    {
        BindingNode binding = setupBasicBind(createBasicObj());
        
        String json = (new JsonBindingSerializer()).serialize(binding);
        
        String expected = ObjUtils.convertSingleQuoteToDouble("{'val1t':'abc','val2t':'def','obj1t':{'xt':123,'yt':456},'list1t':[{'xt':'xyz','yt':'pdq'}]}");
        Assert.assertEquals(expected, json);
        //System.out.println(json);
    }
    
    @Test
    public void testJsonImport() throws Exception
    {
        String json = ObjUtils.convertSingleQuoteToDouble("{'val1t':'abc','val2t':'def','obj1t':{'xt':123,'yt':456},'list1t':[{'xt':'xyz','yt':'pdq'},{'xt':'ABC','yt':'DEF'}]}");
        
        RootObject root = new RootObject();
        BindingNode bind = setupBasicBind(root);
        (new JsonBindingSerializer()).deserialize(bind, json);
        
        RootObject obj = (RootObject)bind.getValue();
        Assert.assertEquals("abc", obj.getVal1());
        Assert.assertEquals("def", obj.getVal2());
        Assert.assertEquals(123, obj.getObj1().getX());
        Assert.assertEquals(456, obj.getObj1().getY());
        Assert.assertEquals("xyz", obj.getList1().get(0).getX());
        Assert.assertEquals("pdq", obj.getList1().get(0).getY());
        Assert.assertEquals("ABC", obj.getList1().get(1).getX());
        Assert.assertEquals("DEF", obj.getList1().get(1).getY());
    }
    
    @Test
    public void testJsonExportList() throws Exception
    {
        List<ChildObj> list = new ArrayList<ChildObj>();
        list.add(new ChildObj(123,456));
        list.add(new ChildObj("abc","def"));
        list.add(new ChildObj("xyz","pdq"));
        BindingNode binding = (new JsonBindingReader()).readListBinding(this.getClass().getResource("list-as-root-binding.txt"), list, ChildObj.class);
        
        String json = (new JsonBindingSerializer()).serialize(binding);
        
        String expected = ObjUtils.convertSingleQuoteToDouble("[{'xt':123,'yt':456},{'xt':'abc','yt':'def'},{'xt':'xyz','yt':'pdq'}]");
        Assert.assertEquals(expected, json);
    }
    
    @Test
    public void testJsonImportList() throws Exception
    {
        String json = ObjUtils.convertSingleQuoteToDouble("[{'xt':123,'yt':456},{'xt':'abc','yt':'def'},{'xt':'xyz','yt':'pdq'}]");
        
        List<ChildObj> list = new ArrayList<ChildObj>();
        BindingNode binding = (new JsonBindingReader()).readListBinding(this.getClass().getResource("list-as-root-binding.txt"), list, ChildObj.class);
        
        (new JsonBindingSerializer()).deserialize(binding, json);
        
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(123, list.get(0).getX());
        Assert.assertEquals(456, list.get(0).getY());
        Assert.assertEquals("abc", list.get(1).getX());
        Assert.assertEquals("def", list.get(1).getY());
        Assert.assertEquals("xyz", list.get(2).getX());
        Assert.assertEquals("pdq", list.get(2).getY());
    }
    
    @Test
    public void testJsonImportConstraintValidation() throws Exception
    {
        String json = ObjUtils.convertSingleQuoteToDouble("{'val1t':'abcd','val2t':'def','obj1t':{'xt':123,'yt':456},'list1t':[{'xt':'xyz','yt':'pdq'},{'xt':'ABC','yt':'DEF'}]}");
        
        RootObjectWithConstraints root = new RootObjectWithConstraints();
        BindingNode node = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("basic-binding.txt"), root);
        (new JsonBindingSerializer()).deserialize(node, json);
        
        Assert.assertEquals(node.getConstraintViolations().size(), 1);
        Assert.assertEquals("size must be between 0 and 3", node.getConstraintViolations().get(0).getOriginal().getMessage());
        Assert.assertEquals("val1", node.getConstraintViolations().get(0).getOriginal().getPropertyPath().toString());
        Assert.assertEquals("val1t", node.getConstraintViolations().get(0).getPath());
        RootObjectWithConstraints obj = (RootObjectWithConstraints)node.getValue();
        Assert.assertNull(obj.getVal1());
        Assert.assertEquals("def", obj.getVal2());
        Assert.assertEquals(123, obj.getObj1().getX());
        Assert.assertEquals(456, obj.getObj1().getY());
        
        
        String json2 = ObjUtils.convertSingleQuoteToDouble("{'val1t':'abc','val2t':'def'}");
        
        RootObjectWithConstraints root2 = new RootObjectWithConstraints();
        BindingNode node2 = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("basic-binding.txt"), root2);
        (new JsonBindingSerializer()).deserialize(node2, json2);
        
        Assert.assertEquals(node2.getConstraintViolations().size(), 1);
        Assert.assertEquals("may not be null", node2.getConstraintViolations().get(0).getOriginal().getMessage());
        Assert.assertEquals("obj1", node2.getConstraintViolations().get(0).getOriginal().getPropertyPath().toString());
        Assert.assertEquals("obj1t", node2.getConstraintViolations().get(0).getPath());
        RootObjectWithConstraints obj2 = (RootObjectWithConstraints)node2.getValue();
        Assert.assertEquals("abc", obj2.getVal1());
        Assert.assertEquals("def", obj2.getVal2());
        Assert.assertNull(obj2.getObj1());
        Assert.assertNull(obj2.getList1());
        
        String json3 = ObjUtils.convertSingleQuoteToDouble("{'val1t':'abcd','val2t':'def','obj1t':{'xt':123,'yt':456},'list1t':[{'xt':'xyz','yt':'pdq'},{'xt':'ABC','yt':null}]}");
        
        RootObjectWithConstraints root3 = new RootObjectWithConstraints();
        BindingNode node3 = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("basic-binding.txt"), root3);
        (new JsonBindingSerializer()).deserialize(node3, json3);
        
        Assert.assertEquals(node3.getConstraintViolations().size(), 2);
        Assert.assertEquals("size must be between 0 and 3", node3.getConstraintViolations().get(0).getOriginal().getMessage());
        Assert.assertEquals("val1", node3.getConstraintViolations().get(0).getOriginal().getPropertyPath().toString());
        Assert.assertEquals("val1t", node3.getConstraintViolations().get(0).getPath());
        Assert.assertEquals("may not be null", node3.getConstraintViolations().get(1).getOriginal().getMessage());
        Assert.assertEquals("y", node3.getConstraintViolations().get(1).getOriginal().getPropertyPath().toString());
        Assert.assertEquals("list1t[1].yt", node3.getConstraintViolations().get(1).getPath());
        
        String json4 = ObjUtils.convertSingleQuoteToDouble("{'val1t':'abcd','val2t':'def','obj1t':{'xt':123,'yt':456},'list1t':[{'xt':'xyz','yt':'pdq'},{'xt':'ABC','yt':null}]}");
        
        RootObjectWithConstraints root4 = new RootObjectWithConstraints();
        BindingNode node4 = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("basic-binding-el.txt"), root4);
        (new JsonBindingSerializer()).deserialize(node4, json4);
        
        Assert.assertEquals(node4.getConstraintViolations().size(), 2);
        Assert.assertEquals("size must be between 0 and 3", node4.getConstraintViolations().get(0).getOriginal().getMessage());
        Assert.assertEquals("val1", node4.getConstraintViolations().get(0).getOriginal().getPropertyPath().toString());
        Assert.assertEquals("val1t", node4.getConstraintViolations().get(0).getPath());
        Assert.assertEquals("may not be null", node4.getConstraintViolations().get(1).getOriginal().getMessage());
        Assert.assertEquals("y", node4.getConstraintViolations().get(1).getOriginal().getPropertyPath().toString());
        Assert.assertEquals("list1t[1].yt", node4.getConstraintViolations().get(1).getPath());
    }
    
    @Test
    public void testJsonExportAndImportWithDummy() throws Exception
    {
        RootObject root = createBasicObj();
        BindingNode binding = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("dummy-node-binding.txt"), root);
        
        String json = (new JsonBindingSerializer()).serialize(binding);
        
        String expected = ObjUtils.convertSingleQuoteToDouble("{'composite':{'val1t':'abc','val2t':'def'},'obj1t':{'xt':123,'yt':456},'list1t':[{'xt':'xyz','yt':'pdq'}]}");
        Assert.assertEquals(expected, json);
        
        RootObject root2 = createBasicObj();
        BindingNode binding2 = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("dummy-node-binding.txt"), root2);
        (new JsonBindingSerializer()).deserialize(binding2, json);
        
        RootObject obj = (RootObject)binding2.getValue();
        Assert.assertTrue(root2==obj);
        Assert.assertEquals("abc", obj.getVal1());
        Assert.assertEquals("def", obj.getVal2());
        Assert.assertEquals(123, obj.getObj1().getX());
        Assert.assertEquals(456, obj.getObj1().getY());
        Assert.assertEquals("xyz", obj.getList1().get(0).getX());
        Assert.assertEquals("pdq", obj.getList1().get(0).getY());
    }
    
    @Test
    public void testJsonExportAndImportOgnlAndEl() throws Exception
    {
        RootObject root = createBasicObj();
        BindingNode binding = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("el-ognl-binding.txt"), root);
        
        String json = (new JsonBindingSerializer()).serialize(binding);
        
        String expected = ObjUtils.convertSingleQuoteToDouble("{'val1t':'abc','val2t':'def','obj1t_x':123,'obj1t_y':456,'obj1t':{'xt':123,'yt':456},'obj1t2':{'xt':123,'yt':456},'list1t':[{'xt':'xyz','yt':'pdq'}],'prop_in_obj_in_list':'xyz','obj_in_list':{'xt':'xyz','yt':'pdq'},'prop_in_obj_in_list2':'xyz','obj_in_list2':{'xt':'xyz','yt':'pdq'}}");
        Assert.assertEquals(expected, json);

        RootObject root2 = createBasicObj();
        BindingNode binding2 = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("el-ognl-binding.txt"), root2);
        (new JsonBindingSerializer()).deserialize(binding2, json);
        Assert.assertEquals("abc", root2.getVal1());
        Assert.assertEquals("def", root2.getVal2());
    }
    
    @Test
    public void testImportEditStyles() throws Exception
    {
        String json = ObjUtils.convertSingleQuoteToDouble("{'listWithIds':[{'id':'2','data':'xyz'},{'id':'1','data':'uvw'}],'listWithoutIds':[{'x':'abc','y':'def'},{'x':'123','y':'456'}]}");
        
        RootForListInputStyleTests root = setupRootForInputStylesTest();
        
        BindingNode node = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("input-style-edit-binding.txt"), root);
        (new JsonBindingSerializer()).deserialize(node, json);
        
        RootForListInputStyleTests obj = (RootForListInputStyleTests)node.getValue();

        Assert.assertEquals(2, obj.getListWithIds().size());
        Assert.assertEquals("1",obj.getListWithIds().get(0).getId());
        Assert.assertEquals("uvw",obj.getListWithIds().get(0).getData());
        Assert.assertEquals("2",obj.getListWithIds().get(1).getId());
        Assert.assertEquals("xyz",obj.getListWithIds().get(1).getData());
        
        Assert.assertEquals(2, obj.getListWithoutIds().size());
        Assert.assertEquals("abc",obj.getListWithoutIds().get(0).getX());
        Assert.assertEquals("def",obj.getListWithoutIds().get(0).getY());
        Assert.assertEquals("123",obj.getListWithoutIds().get(1).getX());
        Assert.assertEquals("456",obj.getListWithoutIds().get(1).getY());
    }

    @Test
    public void testImportHybridStyles() throws Exception
    {
        String json = ObjUtils.convertSingleQuoteToDouble("{'listWithIds':[{'id':'2','data':'xyz'},{'id':'1','data':'uvw'}],'listWithoutIds':[{'x':'abc','y':'def'},{'x':'123','y':'456'}]}");

        RootForListInputStyleTests root = setupRootForInputStylesTest();
        BindingNode node = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("input-style-hybrid-binding.txt"), root);
        (new JsonBindingSerializer()).deserialize(node, json);
        
        RootForListInputStyleTests obj = (RootForListInputStyleTests)node.getValue();
        
        Assert.assertEquals(2, obj.getListWithIds().size());
        Assert.assertEquals("1",obj.getListWithIds().get(0).getId());
        Assert.assertEquals("uvw",obj.getListWithIds().get(0).getData());
        Assert.assertEquals("2",obj.getListWithIds().get(1).getId());
        Assert.assertEquals("xyz",obj.getListWithIds().get(1).getData());
        
        Assert.assertEquals(2, obj.getListWithoutIds().size());
        Assert.assertEquals("abc",obj.getListWithoutIds().get(0).getX());
        Assert.assertEquals("def",obj.getListWithoutIds().get(0).getY());
        Assert.assertEquals("123",obj.getListWithoutIds().get(1).getX());
        Assert.assertEquals("456",obj.getListWithoutIds().get(1).getY());
        
        String json2 = ObjUtils.convertSingleQuoteToDouble("{'listWithIds':[{'id':'2','data':'xyz'},{'id':'3','data':'uvw'},{'data':'new'}],'listWithoutIds':[{'x':'abc','y':'def'},{'x':'123','y':'456'},{'x':'i am','y':'new'}]}");

        RootForListInputStyleTests root2 = setupRootForInputStylesTest();
        BindingNode node2 = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("input-style-hybrid-binding.txt"), root2);
        (new JsonBindingSerializer()).deserialize(node2, json2);
        
        RootForListInputStyleTests obj2 = (RootForListInputStyleTests)node2.getValue();
        Assert.assertEquals(4, obj2.getListWithIds().size());
        Assert.assertEquals("1",obj2.getListWithIds().get(0).getId());
        Assert.assertEquals("111",obj2.getListWithIds().get(0).getData());
        Assert.assertEquals("2",obj2.getListWithIds().get(1).getId());
        Assert.assertEquals("xyz",obj2.getListWithIds().get(1).getData());
        Assert.assertEquals("3",obj2.getListWithIds().get(2).getId());
        Assert.assertEquals("uvw",obj2.getListWithIds().get(2).getData());
        Assert.assertEquals(null,obj2.getListWithIds().get(3).getId());
        Assert.assertEquals("new",obj2.getListWithIds().get(3).getData());
        
        Assert.assertEquals(3, obj2.getListWithoutIds().size());
        Assert.assertEquals("abc",obj2.getListWithoutIds().get(0).getX());
        Assert.assertEquals("def",obj2.getListWithoutIds().get(0).getY());
        Assert.assertEquals("123",obj2.getListWithoutIds().get(1).getX());
        Assert.assertEquals("456",obj2.getListWithoutIds().get(1).getY());
        Assert.assertEquals("i am",obj2.getListWithoutIds().get(2).getX());
        Assert.assertEquals("new",obj2.getListWithoutIds().get(2).getY());
        
        String json3 = ObjUtils.convertSingleQuoteToDouble("{'listWithIds':[],'listWithoutIds':[{'x':'abc','y':'def'}]}");

        RootForListInputStyleTests root3 = setupRootForInputStylesTest();
        BindingNode node3 = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("input-style-hybrid-binding.txt"), root3);
        (new JsonBindingSerializer()).deserialize(node3, json3);
        
        RootForListInputStyleTests obj3 = (RootForListInputStyleTests)node3.getValue();
        Assert.assertEquals(2, obj3.getListWithIds().size());
        
        Assert.assertEquals(1, obj3.getListWithoutIds().size());
        Assert.assertEquals("abc",obj3.getListWithoutIds().get(0).getX());
        Assert.assertEquals("def",obj3.getListWithoutIds().get(0).getY());

    }

    @Test
    public void testImportStylesWithSelectUsingExisting() throws Exception
    {
        String json = ObjUtils.convertSingleQuoteToDouble("{'listWithIds':[{'id':'2','data':'xyz'},{'id':'1','data':'uvw'}]}");

        RootForListInputStyleTests root = setupRootForInputStylesTest();
        BindingNode node = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("input-style-select-binding.txt"), root);
        (new JsonBindingSerializer()).deserialize(node, json);
        
        RootForListInputStyleTests obj = (RootForListInputStyleTests)node.getValue();
        
        Assert.assertEquals(2, obj.getListWithIds().size());
        Assert.assertEquals("2",obj.getListWithIds().get(0).getId());
        Assert.assertEquals("222",obj.getListWithIds().get(0).getData());
        Assert.assertEquals("1",obj.getListWithIds().get(1).getId());
        Assert.assertEquals("111",obj.getListWithIds().get(1).getData());
        
        Assert.assertEquals(2, obj.getListWithoutIds().size());
        Assert.assertEquals("aaa",obj.getListWithoutIds().get(0).getX());
        Assert.assertEquals("aaa",obj.getListWithoutIds().get(0).getY());
        Assert.assertEquals("bbb",obj.getListWithoutIds().get(1).getX());
        Assert.assertEquals("bbb",obj.getListWithoutIds().get(1).getY());

    }
    
    @Test
    public void testImportStylesWithSelectUsingExistingAndDao() throws Exception
    {
        MockDao dao = new MockDao();
        String json = ObjUtils.convertSingleQuoteToDouble("{'listWithIds':[{'id':'2','data':'xyz'},{'id':'1','data':'uvw'},{'id':'3'}]}");

        RootForListInputStyleTests root = setupRootForInputStylesTest();
        BindingNode node = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("input-style-select-binding.txt"), root);
        (new JsonBindingSerializer()).deserialize(node, json, dao);
        
        RootForListInputStyleTests obj = (RootForListInputStyleTests)node.getValue();
        
        Assert.assertEquals(3, obj.getListWithIds().size());
        Assert.assertEquals("2",obj.getListWithIds().get(0).getId());
        Assert.assertEquals("222",obj.getListWithIds().get(0).getData());
        Assert.assertEquals("1",obj.getListWithIds().get(1).getId());
        Assert.assertEquals("111",obj.getListWithIds().get(1).getData());
        Assert.assertEquals("3",obj.getListWithIds().get(2).getId());
        Assert.assertEquals("3-data",obj.getListWithIds().get(2).getData());
        Assert.assertTrue(obj.getListWithIds().get(2)==dao.loadById(ChildWithId.class, "3"));
    }
    
    @Test
    public void testImportStylesWithHybridAndDao() throws Exception
    {
        MockDao dao = new MockDao();
        String json = ObjUtils.convertSingleQuoteToDouble("{'listWithIds':[{'id':'2','data':'xyz'},{'id':'1','data':'uvw'},{'id':'3'}]}");

        RootForListInputStyleTests root = setupRootForInputStylesTest();
        BindingNode node = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("input-style-hybrid-binding.txt"), root);
        (new JsonBindingSerializer()).deserialize(node, json, dao);
        
        RootForListInputStyleTests obj = (RootForListInputStyleTests)node.getValue();
        
        Assert.assertEquals(3, obj.getListWithIds().size());
        Assert.assertEquals("1",obj.getListWithIds().get(0).getId());
        Assert.assertEquals("uvw",obj.getListWithIds().get(0).getData());
        Assert.assertEquals("2",obj.getListWithIds().get(1).getId());
        Assert.assertEquals("xyz",obj.getListWithIds().get(1).getData());
        Assert.assertEquals("3",obj.getListWithIds().get(2).getId());
        Assert.assertEquals("3-data",obj.getListWithIds().get(2).getData());
        Assert.assertTrue(obj.getListWithIds().get(2)==dao.loadById(ChildWithId.class, "3"));
    }
    
    @Test
    public void testReadOnly() throws Exception
    {
        RootObject root1 = createBasicObj();
        BindingNode binding = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("basic-binding-readonly.txt"), root1);
        String json = (new JsonBindingSerializer()).serialize(binding);
        
        String expected = ObjUtils.convertSingleQuoteToDouble("{'val1t':'abc','val2t':'def'}");
        Assert.assertEquals(expected, json);

        String json2 = ObjUtils.convertSingleQuoteToDouble("{'val1t':'UVW','val2t':'XYZ'}");
        RootObject root2 = createBasicObj();
        BindingNode binding2 = (new JsonBindingReader()).readNonListBinding(this.getClass().getResource("basic-binding-readonly.txt"), root2);
        (new JsonBindingSerializer()).deserialize(binding2, json2);
        
        Assert.assertEquals("UVW", root2.getVal1());
        Assert.assertEquals("def", root2.getVal2());
    }

    private RootForListInputStyleTests setupRootForInputStylesTest()
    {
        RootForListInputStyleTests root = new RootForListInputStyleTests();
        root.addChild(new ChildWithId("1","111"));
        root.addChild(new ChildWithId("2","222"));
        root.addChild(new ChildObj("aaa","aaa"));
        root.addChild(new ChildObj("bbb","bbb"));
        return root;
    }
    
    private BindingNode setupBasicBind(Object obj)
    {
        BindingNode bind = Binder.startWithObj(obj)
            .bindValue("val1t", "val1")
            .bindValue("val2t", "val2")
            .bindObject("obj1t", "obj1", ChildObj.class)
                .bindValue("xt", "x")
                .bindValue("yt", "y")
            .endObject()
            .bindList("list1t", "list1", InputStyle.HYBRID_AUTO, ChildObj.class)
                .bindValue("xt", "x")
                .bindValue("yt", "y")
            .endList()
            .getBinding();
        return bind;
    }
    
    private RootObject createBasicObj()
    {
        RootObject obj = new RootObject();
        obj.setVal1("abc");
        obj.setVal2("def");
        obj.setObj1(new ChildObj(123,456));
        obj.addChild(new ChildObj("xyz","pdq"));
        return obj;
    }
}
