//------------------------------------------------------------------------------
//  File       : ProcessStatus.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 03/31/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.itunestop25songs.utils;

public enum ProcessStatus {
    IDLE,
    DOWNLOADING,
    PARSING,
    CANCELLED,
    NOT_INITIALIZED,
    FAILED_EMPTY,
    SUCCESS
}
