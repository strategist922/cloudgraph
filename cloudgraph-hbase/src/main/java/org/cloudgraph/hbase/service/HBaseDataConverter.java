package org.cloudgraph.hbase.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.plasma.sdo.DataType;
import org.plasma.sdo.helper.DataConverter;

import commonj.sdo.Property;

public class HBaseDataConverter {
	private static Log log = LogFactory.getFactory().getInstance(
			HBaseDataConverter.class);

	static public HBaseDataConverter INSTANCE = initializeInstance();

	private HBaseDataConverter() {
	}

	private static synchronized HBaseDataConverter initializeInstance() {
		if (INSTANCE == null)
			INSTANCE = new HBaseDataConverter();
		return INSTANCE;
	}

	public Object fromBytes(Property targetProperty, byte[] value)
	{
		Object result = null;

		if (targetProperty.getType().isDataType()) {
			DataType targetDataType = DataType.valueOf(targetProperty.getType()
					.getName());

			switch (targetDataType) {
			case String:
			case URI:
			case Month:
			case MonthDay:
			case Day:
			case Time:
			case Year:
			case YearMonth:
			case YearMonthDay:
			case Duration:
				result = Bytes.toString(value); 
				break;
			case Date:
				String resultStr = Bytes.toString(value); 
				result = DataConverter.INSTANCE.fromString(targetProperty
						.getType(), resultStr);
				break;
			case DateTime:
				// NOTE: remember datetime is a String Java representation in SDO 2.1
				result = Bytes.toString(value); 
				break;
			case Decimal:
				result = Bytes.toBigDecimal(value); 
				break;
			case Bytes: 
			    // NOTE: there's a Bytes.
				result = value;  
				break;
			case Byte:
				// NOTE: no toByte method as would expect as there is opposite method, see below
				//byte resultByte = Bytes.toByte(value);
				result = value; 
				break;
			case Boolean:
				result = Bytes.toBoolean(value);  
				break;
			case Character:
				resultStr = Bytes.toString(value); 
				result = DataConverter.INSTANCE.toCharacter(targetProperty
						.getType(), resultStr);
				break;
			case Double:
				result = Bytes.toDouble(value);   
				break;
			case Float:
				result = Bytes.toFloat(value);  
				break;
			case Int:
				result = Bytes.toInt(value);  
				break;
			case Integer:
				// FIXME: hack for Toad4CLoud
				result = new BigInteger(value);
				break;
			case Long:
				// FIXME: hack for Toad4CLoud
				//result = Bytes.toLong(value);  
				resultStr = Bytes.toString(value);
				result = Long.valueOf(resultStr);
				break;
			case Short:
				result = Bytes.toShort(value);  
				break;
			case Strings:
				String valueStr = Bytes.toString(value); ;
				String[] values = valueStr.split("\\s");
				List<String> list = new ArrayList<String>(values.length);
				for (int i = 0; i < values.length; i++)
					list.add(values[i]); // what no Java 5 sugar for this ??
				result = list;
				break;
			case Object:
				throw new RuntimeException("not implemented");
			default:
				result = Bytes.toString(value);  
				break;
			}
		} else {
			throw new RuntimeException("not implemented");
		}

		return result;
	}	

	public byte[] toBytes(Property sourceProperty, Object value)
	{
		byte[] result;
		if (sourceProperty.getType().isDataType()) {
			DataType dataType = DataType.valueOf(sourceProperty.getType()
					.getName());
		
			switch (dataType) {
			case String:
			case URI:
			case Month:
			case MonthDay:
			case Day:
			case Time:
			case Year:
			case YearMonth:
			case YearMonthDay:
			case Duration:
				String resultStr = DataConverter.INSTANCE.toString(sourceProperty
						.getType(), value);
				result = Bytes.toBytes(resultStr);
				break;
			case Date:
				// Plasma SDO allows more precision than just month/day/year
				// in an SDO date datatype, and using java.sql.Date will truncate
				// here so use java.sql.Timestamp.
				
				// No Bytes.toBytes(java.util.Date) so convert to SDO
				// String representation first. Note: must then use
				// SDO DataConverter.fromString() for reverse operation
				resultStr = DataConverter.INSTANCE.toString(sourceProperty.getType(), value);
				result = Bytes.toBytes(resultStr);
				break;
			case DateTime:
				resultStr = DataConverter.INSTANCE.toString(sourceProperty.getType(), value);
				result = Bytes.toBytes(resultStr);
				break;
			case Decimal:
				BigDecimal resultDecimal = DataConverter.INSTANCE.toDecimal(sourceProperty.getType(), value);
				result = Bytes.toBytes(resultDecimal);
				break;
			case Bytes:
				byte[] resultBytes = DataConverter.INSTANCE.toBytes(sourceProperty.getType(), value);
				result = resultBytes;
				break;
			case Byte:
				byte resultByte = DataConverter.INSTANCE.toByte(
					sourceProperty.getType(), value);
				result = Bytes.toBytes(resultByte);
				break;
			case Boolean:
				boolean resultBool = DataConverter.INSTANCE.toBoolean(sourceProperty
						.getType(), value);
				result = Bytes.toBytes(resultBool);
				break;
			case Character:
				resultStr = DataConverter.INSTANCE.toString(sourceProperty
						.getType(), value);
				result = Bytes.toBytes(resultStr);
				break;
			case Double:
				double resultDouble = DataConverter.INSTANCE.toDouble(sourceProperty
						.getType(), value);
				result = Bytes.toBytes(resultDouble);
				break;
			case Float:
				float resultFloat = DataConverter.INSTANCE.toFloat(sourceProperty
						.getType(), value);
				result = Bytes.toBytes(resultFloat);
				break;
			case Int:
				int resultInt = DataConverter.INSTANCE.toInt(sourceProperty.getType(),
						value);
				result = Bytes.toBytes(resultInt);
				break;
			case Integer:
				BigInteger resultInteger = DataConverter.INSTANCE.toInteger(sourceProperty
						.getType(), value);
				//NOTE: no  
				result = resultInteger.toByteArray();
				break;
			case Long:
				long resultLong = DataConverter.INSTANCE.toLong(
						    sourceProperty.getType(), value);
				// FIXME: hack for Toad4CLoud
				//result = Bytes.toBytes(resultLong);
				resultStr = DataConverter.INSTANCE.toString(
					    sourceProperty.getType(), value); 
				result = Bytes.toBytes(resultStr);
				break;
			case Short:
				short resultShort = DataConverter.INSTANCE.toShort(sourceProperty
						.getType(), value);
				result = Bytes.toBytes(resultShort);
				break;
			case Strings:
				resultStr = DataConverter.INSTANCE.toString(sourceProperty
						.getType(), value);
				result = Bytes.toBytes(resultStr);
				break;
			case Object:
				throw new RuntimeException("not implemented");
			default:
				resultStr = DataConverter.INSTANCE.toString(sourceProperty
						.getType(), value);
				result = Bytes.toBytes(resultStr);
				break;
			}
		} else {
			throw new RuntimeException("not implemented");
		}
		return result;
	}


}