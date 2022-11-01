package client.halouhuandian.app15.hardWareConncetion.androidHard;

import java.util.ArrayList;

/**
 * 数据注册mode
 * 因为没有返回所有数据给注册类 所有注册的时候需要填写 所需的地址段
 * 里面包含 需要哪个地址段的数据
 *          返回的接口
 */
public class BaseDataRegister {

    //地址段 必须是长度为2的范围数组 比如[0 , 200]这样的 这是最大最小范围 如果范围只有一个 就是[100 , 100]这儿样的
    //没有这个范围段 或者长度为0或者1 肯定是不返回数据的 大于2了 后面的数据也是废弃的
    private ArrayList<long[]> rangeList = new ArrayList();
    private BaseDataReturnListener baseDataReturnListener;

    public BaseDataRegister(ArrayList<long[]> rangeList, BaseDataReturnListener baseDataReturnListener) {
        this.rangeList = rangeList;
        this.baseDataReturnListener = baseDataReturnListener;
    }

    public ArrayList<long[]> getRangeList() {
        return rangeList;
    }

    public BaseDataReturnListener getBaseDataReturnListener() {
        return baseDataReturnListener;
    }
}
