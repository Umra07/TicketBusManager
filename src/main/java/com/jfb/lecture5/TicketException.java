package com.jfb.lecture5;

public enum TicketException implements ExceptionBase {
    TICKET_NOT_FOUND("Ticket not found.");

    private final String msg;

    TicketException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

}
