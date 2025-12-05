package de.berlin.htw.entity.dto;

import java.security.Principal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@Entity
@Table(name = "USER")
public class UserEntity extends AbstractEntity implements Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "BALANCE")
    private Float balance;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }
    
    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
    	if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof UserEntity) {
            if (((UserEntity) other).getId() == getId()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
    	return getId() + ":" + getName();
    }

}
