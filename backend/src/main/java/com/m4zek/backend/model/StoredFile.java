package com.m4zek.backend.model;


import jakarta.persistence.*;

@Entity
@Table(name = "stored_file")
public class StoredFile extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String objectKey;

    private String originalFileName;

    private String contentType;

    private Long size;

    @Enumerated(EnumType.STRING)
    private FileType type;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_data_id", unique = true)
    private UserData userData;

    public StoredFile() {}

    public StoredFile(String objectKey, String originalFileName, String contentType, Long size, FileType type) {
        this.objectKey = objectKey;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.size = size;
        this.type = type;
    }

    public Long getId(){
        return this.id;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSize() {
        return size;
    }

    public FileType getType(){
        return this.type;
    }

    public void assignToCompany(Company company){
        this.company = company;
    }

    public void updateObjectKey(String key){
        this.objectKey = key;
    }

}
