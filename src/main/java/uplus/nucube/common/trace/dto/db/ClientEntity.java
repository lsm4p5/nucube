package uplus.nucube.common.trace.dto.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@Entity
@Getter @Setter
public class ClientEntity {

    @Id
    @GeneratedValue
    @Column(name="clientEntity_id")
    private Long id;

    String uuid;
    String basePackage ;
    int level;
    long startTimeMs;
    long durationTimeMs;
    long callArgCount;
    String callClassType;
    String classAnnotation ;
    String callMethodName;
    String callReturnType;
    String callReturnTypeDepth1;

    String classValidation;
    String beanInfo;


    @ElementCollection
    @CollectionTable(name="ClientArg",joinColumns = @JoinColumn(name="clientEntity_id"))
    private List<ClientArg> clientArgs = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="ClientInput",joinColumns = @JoinColumn(name="clientEntity_id"))
    private List<ClientInput> clientInputs = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="ClientOutput",joinColumns = @JoinColumn(name="clientEntity_id"))
    private List<ClientOutput> clientOutputs = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name="ClientTest",joinColumns = @JoinColumn(name="clientEntity_id"))
    private List<ClientTest> clientTests = new ArrayList<>();



    String name2;



}
