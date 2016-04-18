//------------------------------------------------------------------------------
//  File       : SchemaContract.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 04/15/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.flickerbrowser;

public interface SchemaContract {

    String createSchemaSQL();

    String dropSchemaSQL();

    String createIndexSQL();
}
