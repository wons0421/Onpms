package kr.co.onandon.onpms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Table(name = "MBER")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mber {
    @Id
    @Column(name = "MBER_SN")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mberSn;

    @Column(name = "ID")
    private String mberId;

    @Column(name = "PW")
    private String pw;

    @Column(name = "NAME")
    private String name;

    @Column(name = "AUTH")
    private String auth;
}
