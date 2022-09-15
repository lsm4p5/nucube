package uplus.nucube.common.trace.uplus.db.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uplus.nucube.common.trace.uplus.ClassField;
import uplus.nucube.common.trace.uplus.InputMeta;
import uplus.nucube.common.trace.uplus.TraceCommonUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
@Entity
public class InputMetaEntity {
    @Id
    @GeneratedValue
    @Column(name = "input_id")
    Long id;

    String uuid;
    int inputKinds;// 1 - Primitive, 2 - Wrapper, 3 - Object , 4-Collect
    String classTypeName;
    String simpleTypeName;
    String variable;
    String inputObject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="aopInfo_id")
    private AopClassInfoEntity aopClassInfoEntity;

    // 연관관계 메서드
    public void changeAopClass(AopClassInfoEntity aopClassInfoEntity) {
        this.aopClassInfoEntity = aopClassInfoEntity;
        aopClassInfoEntity.getInputMetaEntities().add( this );
    }


    @ElementCollection
    @CollectionTable(name = "input_class", joinColumns = @JoinColumn(name = "input_id"))
    public List<ClassFieldEntity> fields = new ArrayList<>();

    public void makeInputEntity(InputMeta inputMeta) {
        setUuid( inputMeta.getUuid() );
        setInputKinds( inputMeta.getInputKinds() );
        setClassTypeName( inputMeta.getClassTypeName() );
        setSimpleTypeName( inputMeta.getSimpleTypeName() );
        setVariable( inputMeta.getVariable() );
        setInputObject( TraceCommonUtil.getLimitString( inputMeta.getInputObject()+"" ));
        List<ClassField> fields = inputMeta.getFields();
        for (ClassField field : fields) {
            ClassFieldEntity ent = new ClassFieldEntity();
            ClassFieldEntity classFieldEntity = ent.makeFieldEntity( field );
            getFields().add( classFieldEntity );
        }
    }

    public void printAll() {

        log.info( "InputMeta----------------------" );
        log.info( "id ={}", id );
        log.info( "uuid ={}", uuid );
        log.info( "inputKinds ={}", inputKinds );
        log.info( "classTypeName ={}", classTypeName );
        log.info( "simpleTypeName ={}", simpleTypeName );
        log.info( "variable ={}", variable );
        log.info( "inputObject ={}", inputObject );
        for (ClassFieldEntity field : fields) {
            field.printAll();
        }
        log.info( "InputMeta----------------------" );
    }
}
