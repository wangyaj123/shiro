package com.wyj.shiropro.model;

import java.util.HashSet;
import java.util.Set;

public class Role {

    private Integer rid;

    private String rname;

    private Set<Permission> permissons = new HashSet<>();

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public Set<Permission> getPermissons() {
        return permissons;
    }

    public void setPermissons(Set<Permission> permissons) {
        this.permissons = permissons;
    }

    @Override
    public String toString() {
        return "Role{" +
                "rid=" + rid +
                ", rname='" + rname + '\'' +
                ", permissons=" + permissons +
                '}';
    }
}

