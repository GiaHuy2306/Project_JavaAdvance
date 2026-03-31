package dto;

public class TableChoice {
    private int tableId;
    private String tableName;

    public TableChoice(int tableId, String tableName) {
        this.tableId = tableId;
        this.tableName = tableName;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
