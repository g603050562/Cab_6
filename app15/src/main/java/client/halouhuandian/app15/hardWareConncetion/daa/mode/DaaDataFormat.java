package client.halouhuandian.app15.hardWareConncetion.daa.mode;

import java.util.Arrays;

import client.halouhuandian.app15.hardWareConncetion.daa.mode.acdc.AcdcInfoByStateFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.acdc.AcdcInfoByWarningFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByStateFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByBaseFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByWarningFormat;

public class DaaDataFormat {

    private final int DCDC_MAX = 12;
    private final int ACDC_MAX = 3;

    private DcdcInfoByBaseFormat[] dcdcInfoByBaseFormat = new DcdcInfoByBaseFormat[DCDC_MAX];
    private DcdcInfoByStateFormat[] dcdcInfoByStateFormat = new DcdcInfoByStateFormat[DCDC_MAX];
    private DcdcInfoByWarningFormat[] dcdcInfoByWarningFormats = new DcdcInfoByWarningFormat[DCDC_MAX];
    private AcdcInfoByStateFormat[] acdcInfoByStateFormats = new AcdcInfoByStateFormat[ACDC_MAX];
    private AcdcInfoByWarningFormat[] acdcInfoByWarningFormats = new AcdcInfoByWarningFormat[ACDC_MAX];

    public DaaDataFormat(){
        for(int  i = 0 ; i < DCDC_MAX ; i ++){
            dcdcInfoByBaseFormat[i] = new DcdcInfoByBaseFormat();
            dcdcInfoByStateFormat[i] = new DcdcInfoByStateFormat();
            dcdcInfoByWarningFormats[i] = new DcdcInfoByWarningFormat();
        }
        for(int i = 0 ; i < ACDC_MAX ; i++){
            acdcInfoByStateFormats[i] = new AcdcInfoByStateFormat();
            acdcInfoByWarningFormats[i] = new AcdcInfoByWarningFormat();
        }
    }

    public  DcdcInfoByBaseFormat getDcdcInfoByBaseFormat(int index) {
        return dcdcInfoByBaseFormat[index];
    }

    public  DcdcInfoByBaseFormat[] getDcdcInfoByBaseFormats() {
        return dcdcInfoByBaseFormat;
    }

    public void  setDcdcInfoByBaseFormat(int index , DcdcInfoByBaseFormat mDcdcInfoByBaseFormat) {
        this.dcdcInfoByBaseFormat[index] = mDcdcInfoByBaseFormat;
    }




    public DcdcInfoByStateFormat getDcdcInfoByStateFormat(int index) {
        return dcdcInfoByStateFormat[index];
    }

    public DcdcInfoByStateFormat[] getDcdcInfoByStateFormats() {
        return dcdcInfoByStateFormat;
    }

    public void setDcdcInfoByStateFormats(DcdcInfoByStateFormat[] dcdcInfoByStateFormat) {
        this.dcdcInfoByStateFormat = dcdcInfoByStateFormat;
    }

    public void setDcdcInfoByStateFormat(int index, DcdcInfoByStateFormat dcdcInfoByStateFormat) {
        this.dcdcInfoByStateFormat[index] = dcdcInfoByStateFormat;
    }







    public DcdcInfoByWarningFormat[] getDcdcInfoByWarningFormats() {
        return dcdcInfoByWarningFormats;
    }

    public DcdcInfoByWarningFormat getDcdcInfoByWarningFormat(int index) {
        return dcdcInfoByWarningFormats[index];
    }

    public void setDcdcInfoByWarningFormats(DcdcInfoByWarningFormat[] dcdcInfoByWarningFormats) {
        this.dcdcInfoByWarningFormats = dcdcInfoByWarningFormats;
    }








    public AcdcInfoByStateFormat[] getAcdcInfoByStateFormats() {
        return acdcInfoByStateFormats;
    }
    public AcdcInfoByStateFormat getAcdcInfoByStateFormat(int index) {
        return acdcInfoByStateFormats[index];
    }

    public void setAcdcInfoByStateFormats(AcdcInfoByStateFormat[] acdcInfoByStateFormats) {
        this.acdcInfoByStateFormats = acdcInfoByStateFormats;
    }







    public AcdcInfoByWarningFormat[] getAcdcInfoByWarningFormats() {
        return acdcInfoByWarningFormats;
    }

    public AcdcInfoByWarningFormat getAcdcInfoByWarningFormat(int index) {
        return acdcInfoByWarningFormats[index];
    }

    public void setAcdcInfoByWarningFormats(AcdcInfoByWarningFormat[] acdcInfoByWarningFormats) {
        this.acdcInfoByWarningFormats = acdcInfoByWarningFormats;
    }

    @Override
    public String toString() {
        return "DaaDataFormat{" +
                "dcdcInfoByBaseFormat=" + Arrays.toString(dcdcInfoByBaseFormat) +
                ", dcdcInfoByStateFormat=" + Arrays.toString(dcdcInfoByStateFormat) +
                ", dcdcInfoByWarningFormats=" + Arrays.toString(dcdcInfoByWarningFormats) +
                ", acdcInfoByStateFormats=" + Arrays.toString(acdcInfoByStateFormats) +
                ", acdcInfoByWarningFormats=" + Arrays.toString(acdcInfoByWarningFormats) +
                '}';
    }
}
