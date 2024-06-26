package com.secondwind.prototype.common.entity;//package com.secondwind.prototype.api.domain.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.EntityListeners;
//import jakarta.persistence.MappedSuperclass;
//import java.time.LocalDateTime;
//import lombok.Getter;
//import org.springframework.data.annotation.CreatedBy;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedBy;
//import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//@EntityListeners(AuditingEntityListener.class)
//@MappedSuperclass
//@Getter
//public class CustomBaseEntity {
//
//    @CreatedDate
//    @Column(name = "created_at",updatable = false)
//    private LocalDateTime createdAt;
//
//    @LastModifiedDate
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @CreatedBy
//    @Column(name = "created_by", updatable = false)
//    private String createdBy;
//
//    @LastModifiedBy
//    @Column(name = "updated_by")
//    private String updatedBy;
//}
