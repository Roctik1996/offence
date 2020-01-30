package com.matichuk.offense.model;

public class CarData {
    private String D_REG;

    private String COLOR;

    private String FUEL;

    private String KIND;

    private String MAKE_YEAR;

    private String PURPOSE;

    private String OPER_NAME;

    private String MODEL;

    private String BODY;

    private String BRAND;

    public CarData() {
    }

    public CarData(String d_REG, String COLOR, String FUEL, String KIND, String MAKE_YEAR, String PURPOSE, String OPER_NAME, String MODEL, String BODY, String BRAND) {
        D_REG = d_REG;
        this.COLOR = COLOR;
        this.FUEL = FUEL;
        this.KIND = KIND;
        this.MAKE_YEAR = MAKE_YEAR;
        this.PURPOSE = PURPOSE;
        this.OPER_NAME = OPER_NAME;
        this.MODEL = MODEL;
        this.BODY = BODY;
        this.BRAND = BRAND;
    }

    public String getD_REG ()
    {
        return D_REG;
    }

    public void setD_REG (String D_REG)
    {
        this.D_REG = D_REG;
    }

    public String getCOLOR ()
    {
        return COLOR;
    }

    public void setCOLOR (String COLOR)
    {
        this.COLOR = COLOR;
    }

    public String getFUEL ()
    {
        return FUEL;
    }

    public void setFUEL (String FUEL)
    {
        this.FUEL = FUEL;
    }

    public String getKIND ()
    {
        return KIND;
    }

    public void setKIND (String KIND)
    {
        this.KIND = KIND;
    }

    public String getMAKE_YEAR ()
    {
        return MAKE_YEAR;
    }

    public void setMAKE_YEAR (String MAKE_YEAR)
    {
        this.MAKE_YEAR = MAKE_YEAR;
    }

    public String getPURPOSE ()
    {
        return PURPOSE;
    }

    public void setPURPOSE (String PURPOSE)
    {
        this.PURPOSE = PURPOSE;
    }

    public String getOPER_NAME ()
    {
        return OPER_NAME;
    }

    public void setOPER_NAME (String OPER_NAME)
    {
        this.OPER_NAME = OPER_NAME;
    }

    public String getMODEL ()
    {
        return MODEL;
    }

    public void setMODEL (String MODEL)
    {
        this.MODEL = MODEL;
    }

    public String getBODY ()
    {
        return BODY;
    }

    public void setBODY (String BODY)
    {
        this.BODY = BODY;
    }

    public String getBRAND ()
    {
        return BRAND;
    }

    public void setBRAND (String BRAND)
    {
        this.BRAND = BRAND;
    }

    @Override
    public String toString() {
        return "CarData{" +
                "D_REG='" + D_REG + '\'' +
                ", COLOR='" + COLOR + '\'' +
                ", FUEL='" + FUEL + '\'' +
                ", KIND='" + KIND + '\'' +
                ", MAKE_YEAR='" + MAKE_YEAR + '\'' +
                ", PURPOSE='" + PURPOSE + '\'' +
                ", OPER_NAME='" + OPER_NAME + '\'' +
                ", MODEL='" + MODEL + '\'' +
                ", BODY='" + BODY + '\'' +
                ", BRAND='" + BRAND + '\'' +
                '}';
    }
}
