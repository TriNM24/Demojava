package fpt.isc.nshreport.models;

/**
 * Created by Chick on 12/18/2017.
 */

public class DebtReport {
    private String date;
    private String totalMoney;
    private boolean isHeader;

    private boolean isApproved;
    private String dateHour;
    private String moneyPerDebt;
    private String note;
    private String imageReport;

    public DebtReport() {
    }

    public DebtReport(String date, String totalMoney, boolean isHeader, boolean isApproved, String dateHour, String moneyPerDebt, String note) {
        this.date = date;
        this.totalMoney = totalMoney;
        this.isHeader = isHeader;
        this.isApproved = isApproved;
        this.dateHour = dateHour;
        this.moneyPerDebt = moneyPerDebt;
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getDateHour() {
        return dateHour;
    }

    public void setDateHour(String dateHour) {
        this.dateHour = dateHour;
    }

    public String getMoneyPerDebt() {
        return moneyPerDebt;
    }

    public void setMoneyPerDebt(String moneyPerDebt) {
        this.moneyPerDebt = moneyPerDebt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImageReport() {
        return imageReport;
    }

    public void setImageReport(String imageReport) {
        this.imageReport = imageReport;
    }
}
