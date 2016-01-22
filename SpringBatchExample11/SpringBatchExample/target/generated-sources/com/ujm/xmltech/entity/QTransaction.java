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

    public static final QTransaction transaction = new QTransaction("transaction");

    public QTransaction(String variable) {
        super(Transaction.class, forVariable(variable));
    }

    public QTransaction(Path<? extends Transaction> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QTransaction(PathMetadata<?> metadata) {
        super(Transaction.class, metadata);
    }

}

