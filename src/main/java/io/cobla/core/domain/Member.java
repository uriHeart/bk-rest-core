package io.cobla.core.domain;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="auth_user")
public class Member {
     @Id
     @GeneratedValue
     Long id;
     String username;
     public Member() {}
     public Member( String username) {
         this.username = username;
     }
}