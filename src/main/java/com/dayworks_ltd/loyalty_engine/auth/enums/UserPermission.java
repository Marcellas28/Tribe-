package com.dayworks_ltd.loyalty_engine.auth.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserPermission {
    LEAD_WRITE("lead:write"),
    LEAD_READ("lead:read"),

    USER_WRITE("user:write"),
    USER_READ("user:read"),
    USER_UPDATE("user:update"),

    INVENTORY_GET_ALL("inventory:read_all"),
    INVENTORY_IMPORT("inventory:import"),
    INVENTORY_SALE("inventory:sale"),
    INVENTORY_WRITE_DEDUCTION("inventory:write_deduction"),
    INVENTORY_ADD_STOCK("inventory:add_stock"),
    INVENTORY_RECORD_EXPENSE("inventory:record_expense"),
    INVENTORY_CLOSE_DAY("inventory:close_day"),
    INVENTORY_DAILY_SUMMARY("inventory:daily_summary"),
    INVENTORY_WEEKLY_SUMMARY("inventory:weekly_summary"),
    INVENTORY_REPORT("inventory:get_report"),

    CAMPAIGN_CREATE("campaign:create"),
    CAMPAIGN_UPDATE("campaign:update"),
    CAMPAIGN_GET_ALL("campaign:get_all"),
    CAMPAIGN_GET_BY_ID("campaign:get_by_id"),
    CAMPAIGN_DELETE("campaign:delete"),

    MERCHANT_CREATE("merchant:create"),
    MERCHANT_UPDATE("merchant:update"),
    MERCHANT_GET_ALL("merchant:get_all"),
    MERCHANT_GET_BY_ID("merchant:get_by_id"),
    MERCHANT_GET_BY_TILL_NUMBER("merchant:get_by_till_number"),
    MERCHANT_DELETE("merchant:delete");


    @Getter
    private final String userPermission;
}
