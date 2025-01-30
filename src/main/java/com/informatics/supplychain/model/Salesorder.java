package com.informatics.supplychain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.informatics.supplychain.dto.SalesorderDto;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
public class Salesorder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value = "id")
    @Column(insertable = true, updatable = false)
    protected Integer id;
    private String salesorderNo;
    @NotNull(message = "Date cannot be null.")
    private LocalDate orderDate;
    private String remarks;

    @JoinColumn
    @ManyToOne
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private TransactionStatusEnum status;
    private String createdBy;
    private LocalDateTime createdDateTime;

    @OneToMany(mappedBy = "salesorder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SalesorderDetail> details;

    public Salesorder() {
        status = TransactionStatusEnum.DRAFT;
        createdDateTime = LocalDateTime.now();
    }

    public Salesorder(SalesorderDto dto) {
        salesorderNo = dto.getSalesorderNo();
        orderDate = dto.getOrderDate();
        remarks = dto.getRemarks();
        if (dto.getCustomer() != null) {
            customer = new Customer(dto.getCustomer());
        }
    }
}
