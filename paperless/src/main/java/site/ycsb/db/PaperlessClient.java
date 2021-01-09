package site.ycsb.db;

import java.lang.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import paperless_swig.*;

import site.ycsb.ByteArrayByteIterator;
import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.DBException;
import site.ycsb.Status;

/**
 * A database interface layer for Oracle NoSQL Database.
 */
public class PaperlessClient extends DB {
    private long read_buffer_length = 2000;
    private String read_buffer = String.format("%0" + read_buffer_length + "d", 0);
	
    @Override
    public void init() throws DBException {
         System.loadLibrary("paperless_swig");
         paperless.kv_init();
    }

    @Override    public void cleanup() throws DBException {
         paperless.kv_finalize();
    }	

    @Override
    public Status scan(String table, String startkey, int recordcount, Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
	    System.err.println("Paperless/Papyrus doesn't have the Java interface for Scan semantics implemented yet");
	    return Status.ERROR;
    }

    @Override
    public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
        System.out.println("read: " + key);
        paperless.kv_get(table + key, key.length(), read_buffer, read_buffer.length()); 
	    return Status.OK;
    }

    @Override
    public Status update(String table, String key, Map<String, ByteIterator> values) {
        // Read the value to sort of simulate a true update...
        read(table, key, new HashSet<String>(), new HashMap<String, ByteIterator>()); 
	    return insert(table, key, values);
    }

    @Override
    public Status insert(String table, String key, Map<String, ByteIterator> values) {
        System.out.println("insert/update: " + key);
        String value = "";
	    for (Map.Entry<String, ByteIterator> entry : values.entrySet()) {
            value += entry.getKey() + new String(entry.getValue().toArray());
	    }
        paperless.kv_put(table + key, key.length(), value, value.length()); 
	    return Status.OK;
    }
    
    @Override
    public Status delete(String table, String key) {
	    return Status.OK;
    }
}
