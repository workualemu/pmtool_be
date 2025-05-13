package com.wojet.pmtool.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
