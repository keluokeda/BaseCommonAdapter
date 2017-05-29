# BaseCommonAdapter
 适用于Listview GridView 的通用 adapter

## 怎么集成？
- 在你的项目级别的gradle文件中 添加如下代码
```allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

- 在app 的gradle文件中添加
```
    compile 'com.github.keluokeda.BaseCommonAdapter:basecommomadapter:1.0.4'
    annotationProcessor 'com.github.keluokeda.BaseCommonAdapter:basecommonadapter-compiler:1.0.4'
```

## 怎么使用？
- 定义一个item 实体类
```java
@Item(resource = R.layout.item_user)
public class User {
    private String name;
    private String sign;

    public User(String name, String sign) {
        this.name = name;
        this.sign = sign;
    }

    @Bind(targetId = R.id.tv_name, binderClassName = "com.keluokeda.basecommomadapter.TextViewValueBinder", viewClassName = "android.widget.TextView")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bind(targetId = R.id.tv_sign, binderClassName = "com.keluokeda.basecommomadapter.TextViewValueBinder", viewClassName = "android.widget.TextView")
    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
```
用 注解 @Item 表示这是一个 和ListView 相关联的实体类，resource 用来指定 ListView的item布局

@Bind 注解表示这个方法的返回值会被注入item 里面的一个view上，targetId表示要绑定的view的id，binderClassName 指定 数据绑定器的类全名，viewClassName 指定 view 的类全名

#### 数据绑定器要实现ValueBinder 接口，实现类要包含一个无参的构造方法
```java
public interface ValueBinder<View, Value> {
    void setValue(View view, Value value);
}
```

默认提供TextViewValueBinder
```java
public class TextViewValueBinder implements ValueBinder<TextView, String> {
    @Override
    public void setValue(TextView textView, String charSequence) {
        textView.setText(charSequence);
    }
}
```

- 在你的activity中java代码如下
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.lv_user);


        List<User> users = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            String name = String.valueOf(i);
            String sign = UUID.randomUUID().toString();
            User user = new User(name, sign);
            users.add(user);
        }

        BaseCommonAdapter<User> baseCommonAdapter = AdapterFactory.createAdapter(User.class, users);
        listView.setAdapter(baseCommonAdapter);

    }
}
```

