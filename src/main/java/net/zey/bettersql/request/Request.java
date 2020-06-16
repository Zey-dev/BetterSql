package net.zey.bettersql.request;

import net.zey.bettersql.H;
import net.zey.bettersql.arguments.TableArguments;
import net.zey.bettersql.arguments.TableArgumentsType;
import net.zey.bettersql.condition.ClassicCondition;
import net.zey.bettersql.condition.Condition;
import net.zey.bettersql.condition.DateCondition;
import net.zey.bettersql.help.Symbol;
import net.zey.bettersql.help.SQLObject;
import net.zey.bettersql.database.Table;
import net.zey.bettersql.help.SqlResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Request {

    private StringBuilder sql;
    private Table table;
    private Condition condition;

    public Request(Table table, StringBuilder sql){
        this.table = table;
        this.sql = sql;
    }

    public abstract SqlResult sendSql();

    public Request where(String name, SQLObject sql){
        setCondition(new ClassicCondition(name, sql, Symbol.EQU));
        return this;
    }

    public Request where(String name, SQLObject sql, Symbol symbol){
        setCondition(new ClassicCondition(name,sql, symbol));
        return this;
    }

    public Request where(String column, boolean isOutdated){
        setCondition(new DateCondition(column, isOutdated));
        return this;
    }

    public void set(int i, SQLObject o, PreparedStatement ps){
        try{
            if(o.isInt()){
                ps.setInt(i, o.getInt());
            }else if(o.isDate()){
                ps.setDate(i, o.getDate());
            }else if(o.isString()){
                ps.setString(i, o.getString());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public SQLObject get(TableArguments arg, ResultSet resultSet) throws SQLException {
        TableArgumentsType tp = arg.getType();

        if(tp == TableArgumentsType.VARCHAR || tp == TableArgumentsType.TEXT){
            return H.ob(resultSet.getString(arg.getName()));
        }else if(tp == TableArgumentsType.INT){
            return H.ob(resultSet.getInt(arg.getName()));
        }else if(tp == TableArgumentsType.DATE) {
            return H.ob(resultSet.getDate(arg.getName()));
        }
        return null;
    }

    public StringBuilder getSql() {
        return sql;
    }

    public void setSql(StringBuilder sql) {
        this.sql = sql;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
