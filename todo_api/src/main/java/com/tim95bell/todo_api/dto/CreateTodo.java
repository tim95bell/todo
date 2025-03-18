
package com.tim95bell.todo_api.dto;

public class CreateTodo {
    public CreateTodo(String username, String text, boolean complete) {
        this.username = username;
        this.text = text;
        this.complete = complete;
    }

    public String username;
    public String text;
    public boolean complete;
}
