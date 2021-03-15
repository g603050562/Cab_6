package client.halouhuandian.app15;

import java.util.HashSet;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/6/20
 * Description:
 */
public class TestAccount extends HashSet<String> {
    private final static TestAccount TEST_ACCOUNT = new TestAccount();

    private TestAccount() {
        add("5DAPEF3L");
    }

    public static TestAccount getInstance() {
        return TEST_ACCOUNT;
    }
}
