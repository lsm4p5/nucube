package uplus.nucube.common.trace.uplus.db.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uplus.nucube.common.trace.uplus.ClassField;
import uplus.nucube.common.trace.uplus.OutputMeta;
import uplus.nucube.common.trace.uplus.TraceCommonUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
@Entity
public class OutputMetaEntity {
    @Id
    @GeneratedValue
    @Column(name = "output_id")
    Long id;

    String uuid;
    int outputKinds;// 1 - Primitive, 2 - Wrapper, 3 - Object , 4-Collect
    String classTypeName;
    String simpleTypeName;
    String variable;
    String outputObject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aopInfo_id")
    private AopClassInfoEntity aopClassInfoEntity;

    public void changeAopClass(AopClassInfoEntity aopClassInfoEntity) {
        this.aopClassInfoEntity = aopClassInfoEntity;
        aopClassInfoEntity.getOutputMetaEntities().add( this );
    }

    @ElementCollection
    @CollectionTable(name = "output_class", joinColumns = @JoinColumn(name = "output_id"))
    List<ClassFieldEntity> fields = new ArrayList<>();

    public void makeOutputEntity(OutputMeta outputMeta) {
        setUuid( outputMeta.getUuid() );
        setClassTypeName( outputMeta.getClassTypeName() );
        setOutputKinds( outputMeta.getOutputKinds() );
        setSimpleTypeName( outputMeta.getSimpleTypeName() );
        setVariable( outputMeta.getVariable() );
        setOutputObject( TraceCommonUtil.getLimitString( outputMeta.getOutputObject()+"" ));
        List<ClassField> fields = outputMeta.getFields();
        for (ClassField field : fields) {
            ClassFieldEntity ent = new ClassFieldEntity();
            ClassFieldEntity classFieldEntity = ent.makeFieldEntity( field );
            getFields().add( classFieldEntity );
        }
    }

    public void printAll() {

        log.info( "OutputMeta----------------------" );
        log.info( "id ={}", id );
        log.info( "uuid ={}", uuid );
        log.info( "outputKinds ={}", outputKinds );
        log.info( "classTypeName ={}", classTypeName );
        log.info( "simpleTypeName ={}", simpleTypeName );
        log.info( "variable ={}", variable );
        log.info( "outputObject ={}", outputObject );
        for (ClassFieldEntity field : fields) {
            field.printAll();
        }
        log.info( "OutputMeta----------------------" );
    }
}
