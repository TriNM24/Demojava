package fpt.isc.nshreport.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhamTruong on 31/05/2017.
 */

public class GasPumps implements Serializable {
    private static final long SerialVersionUID = 1L;
    private int id;
    private String code;
    private String name;
    private int numberLine;
    private int litreTotal;
    private long amountTotal;
    private String pumpStatus;
    private String setupDate;
    private ArrayList<PumpLinesAdd> pumpList;

    public ArrayList<PumpLinesAdd> getPumpList() {
        return pumpList;
    }

    public void setPumpList(ArrayList<PumpLinesAdd> pumpList) {
        this.pumpList = pumpList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberLine() {
        return numberLine;
    }

    public void setNumberLine(int numberLine) {
        this.numberLine = numberLine;
    }

    public int getLitreTotal() {
        return litreTotal;
    }

    public void setLitreTotal(int litreTotal) {
        this.litreTotal = litreTotal;
    }

    public long getAmountTotal() {
        return amountTotal;
    }

    public void setAmountTotal(long amountTotal) {
        this.amountTotal = amountTotal;
    }

    public String getPumpStatus() {
        return pumpStatus;
    }

    public void setPumpStatus(String pumpStatus) {
        this.pumpStatus = pumpStatus;
    }

    public String getSetupDate() {
        return setupDate;
    }

    public void setSetupDate(String setupDate) {
        this.setupDate = setupDate;
    }
}
