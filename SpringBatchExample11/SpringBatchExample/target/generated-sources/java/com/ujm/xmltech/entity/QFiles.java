package com.ujm.xmltech.entity;


import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QPain008File is a Querydsl query type for Pain008File
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QFiles extends EntityPathBase<Files>{
	

	

	    private static final long serialVersionUID = -805364681;

	    public static final QFiles Files = new QFiles("Files");

	    public final StringPath country = createString("country");

	    public final StringPath email = createString("email");

	    public final NumberPath<Long> id = createNumber("id", Long.class);

	    public final StringPath msgId = createString("msgId");

	    public final StringPath nameHeader = createString("nameHeader");

	    public final StringPath streetHeader = createString("streetHeader");

	    public final StringPath townHeader = createString("townHeader");

	    public final ListPath<Transaction, QTransaction> transaction = this.<Transaction, QTransaction>createList("transaction", Transaction.class, QTransaction.class);

	    public QFiles(String variable) {
	        super(Files.class, forVariable(variable));
	    }

	    public QFiles(Path<? extends Files> entity) {
	        super(entity.getType(), entity.getMetadata());
	    }

	    public QFiles(PathMetadata<?> metadata) {
	        super(Files.class, metadata);
	    }

}
