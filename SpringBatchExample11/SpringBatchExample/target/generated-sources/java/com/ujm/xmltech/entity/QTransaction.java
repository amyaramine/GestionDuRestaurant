//package com.ujm.xmltech.entity;
//
//import static com.mysema.query.types.PathMetadataFactory.*;
//
//import com.mysema.query.types.*;
//import com.mysema.query.types.path.*;
//
//import javax.annotation.Generated;
//
//
///**
// * QTransaction is a Querydsl query type for Transaction
// */
//@Generated("com.mysema.query.codegen.EntitySerializer")
//public class QTransaction extends EntityPathBase<Transaction> {
//
//    private static final long serialVersionUID = -392116521;
//
//    public static final QTransaction transaction = new QTransaction("transaction");
//
//    public final NumberPath<Long> amount = createNumber("amount", Long.class);
//
//    public final StringPath endToEndId = createString("endToEndId");
//    
//    public final StringPath mndtId = createString("mndtId");
//    
//    public final StringPath msgId = createString("msgId");
//    
//    public final BooleanPath proceced = createBoolean("proceced");
//    
//    public final QFiles File;
//    
////    public final StringPath dateOfSgntr = createString("dateOfSgntr");
////    
////    public final StringPath ibanDeptor = createString("ibanDeptor");
////    
////    public final StringPath biccDeptor = createString("biccDeptor");
//
//    public final NumberPath<Long> id = createNumber("id", Long.class);
//
//	
//
//    public QTransaction(String variable) {
//        super(Transaction.class, forVariable(variable));
//    }
//
//    public QTransaction(Path<? extends Transaction> entity) {
//        super(entity.getType(), entity.getMetadata());
//    }
//
//    public QTransaction(PathMetadata<?> metadata) {
//        super(Transaction.class, metadata);
//    }
//    
////    public QTransaction(Class<? extends Transaction> type, PathMetadata<?> metadata, PathInits inits) {
////        super(type, metadata, inits);
////        this.file = inits.isInitialized("file") ? new QPain008File(forProperty("file")) : null;
////    }
//    
//    public QTransaction(Class<? extends Transaction> type, PathMetadata<?> metadata, PathInits inits) {
//        super(type, metadata, inits);
//        this.file = inits.isInitialized("file") ? new QFiles(forProperty("file")) : null;
//    }
//}


package com.ujm.xmltech.entity;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QTransaction is a Querydsl query type for Transaction
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTransaction extends EntityPathBase<Transaction> {

    private static final long serialVersionUID = -392116521;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QTransaction transaction = new QTransaction("transaction");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final StringPath BIC_creditor = createString("BIC_creditor");

    public final StringPath BIC_debitor = createString("BIC_debitor");

    public final BooleanPath done = createBoolean("done");

    public final StringPath dtOfSgntr = createString("dtOfSgntr");

    public final StringPath endToEndId = createString("endToEndId");

    public final QFiles file;

    public final StringPath IBAN_creditor = createString("IBAN_creditor");

    public final StringPath IBAN_debitor = createString("IBAN_debitor");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mndtId = createString("mndtId");

    public final StringPath pmtInf = createString("pmtInf");

    public final BooleanPath proceced = createBoolean("proceced");

    public final StringPath seqTp = createString("seqTp");

    public final BooleanPath transfer = createBoolean("transfer");

    public QTransaction(String variable) {
        this(Transaction.class, forVariable(variable), INITS);
    }

    public QTransaction(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTransaction(PathMetadata<?> metadata, PathInits inits) {
        this(Transaction.class, metadata, inits);
    }

    public QTransaction(Class<? extends Transaction> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.file = inits.isInitialized("file") ? new QFiles(forProperty("file")) : null;
    }

}




