package cn.lunadeer.lagrangeMC.utils.databse.FIelds;


import cn.lunadeer.lagrangeMC.utils.databse.DatabaseManager;

import java.util.List;

public class FieldFloat extends Field<Float> {

    private Float value;

    public FieldFloat(String name) {
        super(name);
    }

    public FieldFloat(String name, Float value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getSqlTypeStr() {
        return getTypeStrings().get(0);
    }

    @Override
    public String getUnifyTypeStr() {
        return "FLOAT";
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public Field<Float> setValue(Float value) {
        this.value = value;
        return this;
    }

    public static List<String> getTypeStrings() {
        return switch (DatabaseManager.instance.getType()) {
            case MYSQL -> List.of("FLOAT");
            case SQLITE -> List.of("REAL");
            case PGSQL -> List.of("REAL");
            default ->
                    throw new UnsupportedOperationException("Database type: " + DatabaseManager.instance.getType() + " not supported FieldFloat");
        };
    }
}
