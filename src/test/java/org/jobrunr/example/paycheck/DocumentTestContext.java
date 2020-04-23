package org.jobrunr.example.paycheck;

public class DocumentTestContext {

    private final String name;
    private final String foo;
    private final DocumentChildTestContext child;

    public DocumentTestContext(String name, String foo, String childName) {
        this.name = name;
        this.foo = foo;
        this.child = new DocumentChildTestContext(childName);
    }

    public String getName() {
        return name;
    }

    public String getFoo() {
        return foo;
    }

    public DocumentChildTestContext getChild() {
        return child;
    }
}
