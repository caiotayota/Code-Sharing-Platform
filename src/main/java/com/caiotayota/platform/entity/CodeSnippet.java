package com.caiotayota.platform.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class CodeSnippet {
    
    @Id
    @JsonIgnore
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Getter
    private String id;
    
    @Getter
    @Setter
    @JsonProperty
    private String code;
    
    
    @Getter
    @Setter
    private long time;
    
    @Getter
    @Setter
    private long views;
    
    @Getter
    @Setter
    @JsonIgnore
    private boolean viewRestricted;
    
    @Getter
    @Setter
    @JsonIgnore
    private boolean timeRestricted;
    
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
}
