package site.ycsb.db;

import java.lang.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import mdhim.*;

import site.ycsb.ByteArrayByteIterator;
import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.DBException;
import site.ycsb.Status;

import org.bridj.BridJ;
import org.bridj.Pointer;

/**
 * A database interface layer for Oracle NoSQL Database.
 */
public class MDHIMClient extends DB {
	
    private MdhimLibrary mlib;
    Pointer<mdhim_t> md;
    
    @Override
    public void init() throws DBException {


	int num_keys = 10;
	int long_size = 8;

	mlib = new MdhimLibrary();
	md = mlib.mdhimInit(null, null);

        Long key = new Long(100);
		  Long value = new Long(200);

		  Pointer<Long> key_ptr = Pointer.allocate(Long.class);
		  key_ptr.set(key);
		  Pointer<Long> value_ptr = Pointer.allocate(Long.class);
		  value_ptr.set(value);
		  Pointer<mdhim_brm_t > brm = mlib.mdhimPut(md, key_ptr, long_size, value_ptr, long_size, null, null);
		  Pointer<mdhim_bgetrm_t > bgrm = mlib.mdhimGet(md, null, key_ptr, long_size, mlib.MDHIM_GET_EQ);
		  mdhim_bgetrm_t b = bgrm.get();
		  Pointer<Pointer<?>> ptrs = b.values();
		  System.out.println(b);
		  int count = 0;
		  for (Pointer<?> lg : ptrs) {
		  if (count >= b.num_keys()) {
		  break;
		  }

		  System.out.println(lg.getLong());
		  count++;
		  }
        		
    }

    @Override    public void cleanup() throws DBException {
	mlib.mdhimClose(md);
    }	

    @Override
    public Status scan(String table, String startkey, int recordcount, Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
	System.err.println("MDHIM doesn't have the Java interface for Scan semantics implemented yet");
	return Status.ERROR;
    }

    @Override
    public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
	//Only rank 0 reads for now 
	//if(mlib.mdhimRank(md) != 0) {
	//return OK;
	//}

	try {
	    byte[] stringBytes=key.getBytes("US-ASCII");
	    Pointer<Byte> key_ptr = Pointer.allocateBytes(stringBytes.length);
	    key_ptr.setBytes(stringBytes);
	    Pointer<mdhim_bgetrm_t > bgrm = mlib.mdhimGet(md, null, key_ptr, 
							  stringBytes.length, 
							  mlib.MDHIM_GET_EQ);
	    if (bgrm == null) {
		return Status.OK;
	    }

	    //System.out.println("Attempting to get value for key: " + Arrays.toString(stringBytes));
	    mdhim_bgetrm_t b = bgrm.get();
	    //	    System.out.println("Got: " + b + " from mdhimGet");
	    Pointer<Pointer<? > > ptrs = b.values();
	    //System.out.println("Got: " + ptrs + " from mdhimGet");
	    /*   Pointer<Pointer<?>> ptrs = b.values();
		 System.out.println("Got: " + b.values().getBytes() + " from mdhimGet");*/
	    int count = 0;

	    //System.out.println("Got: " + Arrays.toString(ptrs.getBytes(1)) + " from mdhimGet");

	    	    for (Pointer<? > lg : ptrs) {
		System.out.println("Key: " + key + " value: " + lg.get());
				result.put(key, new ByteArrayByteIterator(lg.getBytes()));

		if (count >= b.num_keys()) {
		    break;
		}
		
				System.out.println("Key: " + key + " value: " + lg.getBytes());
				result.put(key, new ByteArrayByteIterator(lg.getBytes()));
				} 
	    
	    mlib.mdhim_full_release_msg(bgrm);
	} catch (Exception e) {
				System.out.println(e.getCause());
	}
	return Status.OK;
    }

    @Override
    public Status update(String table, String key, Map<String, ByteIterator> values) {

	return insert(table, key, values);
    }

    @Override
    public Status insert(String table, String key, Map<String, ByteIterator> values) {
	//Only rank 0 inserts
	//if(mlib.mdhimRank(md) != 0) {
	//    return OK;
	//}
	
	try {
	    byte[] stringBytes=key.getBytes("US-ASCII");
	    Pointer<Byte> key_ptr = Pointer.allocateBytes(stringBytes.length);
	    key_ptr.setBytes(stringBytes);
	
	    byte[] value = new byte[1000];
	    int v = 0;
	    System.out.println("Insert values: " + values); 
	    for (Map.Entry<String, ByteIterator> entry : values.entrySet()) {
		byte[] new_value = entry.getValue().toArray();
		System.arraycopy(new_value, 0, value, v*100, new_value.length);
		v++;
	    }

	    //System.out.println("Inserting key: " + Arrays.toString(stringBytes) + " and value: " + Arrays.toString(value));
	    Pointer<Byte> value_ptr = Pointer.allocateBytes(value.length);
	    value_ptr.setBytes(value);

	    //System.out.println("Attempting to insert a key/value pair. Key is: " + stringBytes); 
	    Pointer<mdhim_brm_t > brm = mlib.mdhimPut(md, key_ptr, stringBytes.length, 
						      value_ptr, value.length, null, null);
	    mlib.mdhim_full_release_msg(brm);
	} catch (Exception e) {

				System.out.println(e);
	}
	return Status.OK;
    }
    
    @Override
    public Status delete(String table, String key) {
	return Status.OK;
    }
}
