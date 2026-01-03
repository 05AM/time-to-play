package org.example.batch.model;

import java.util.ArrayList;
import java.util.List;

public class MemberSaleMail {
    private final long memberId;
    private final String email;
    private final String name;
    private final List<WishlistSaleRow> items = new ArrayList<>();

    public MemberSaleMail(long memberId, String email, String name) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
    }

    public long memberId() {
        return memberId;
    }

    public String email() {
        return email;
    }

    public String name() {
        return name;
    }

    public List<WishlistSaleRow> items() {
        return items;
    }

    public void add(WishlistSaleRow row) {
        items.add(row);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
