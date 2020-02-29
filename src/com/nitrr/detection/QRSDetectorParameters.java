
package com.nitrr.detection;

public class QRSDetectorParameters 
	{

	/** Sample rate in Hz. */
	public int    SAMPLE_RATE ;
	public double MS_PER_SAMPLE ;
	public int MS10 ;
	public int MS25 ;
	public int MS30 ;
	public int MS80 ;
	public int MS95 ;
	public int MS100 ;
	public int MS125 ;
	public int MS150 ;
	public int MS160 ;
	public int MS175 ;
	public int MS195 ;
	public int MS200 ;
	public int MS220 ;
	public int MS250 ;
	public int MS300 ;
	public int MS360 ;
	public int MS450 ;
	public int MS1000 ;
	public int MS1500 ;
	public int DERIV_LENGTH ;
	public int LPBUFFER_LGTH ;
	public int HPBUFFER_LGTH ;
	/** Moving window integration width. */
	public final int WINDOW_WIDTH ; // 
	
	public QRSDetectorParameters(int sampleRate) 
		{
		SAMPLE_RATE   = sampleRate ;
		MS_PER_SAMPLE = ( (double) 1000/ (double) SAMPLE_RATE) ;
		MS10          = ((int) (10/ MS_PER_SAMPLE + 0.5)) ;
		MS25          = ((int) (25/MS_PER_SAMPLE + 0.5)) ;
		MS30          = ((int) (30/MS_PER_SAMPLE + 0.5)) ;
		MS80          = ((int) (80/MS_PER_SAMPLE + 0.5)) ;
		MS95          = ((int) (95/MS_PER_SAMPLE + 0.5)) ;
		MS100         = ((int) (100/MS_PER_SAMPLE + 0.5)) ;
		MS125         = ((int) (125/MS_PER_SAMPLE + 0.5)) ;
		MS150         = ((int) (150/MS_PER_SAMPLE + 0.5)) ;
		MS160         = ((int) (160/MS_PER_SAMPLE + 0.5)) ;
		MS175         = ((int) (175/MS_PER_SAMPLE + 0.5)) ;
		MS195         = ((int) (195/MS_PER_SAMPLE + 0.5)) ;
		MS200         = ((int) (200/MS_PER_SAMPLE + 0.5)) ;
		MS220         = ((int) (220/MS_PER_SAMPLE + 0.5)) ;
		MS250         = ((int) (250/MS_PER_SAMPLE + 0.5)) ;
		MS300         = ((int) (300/MS_PER_SAMPLE + 0.5)) ;
		MS360         = ((int) (360/MS_PER_SAMPLE + 0.5)) ; 
		MS450         = ((int) (450/MS_PER_SAMPLE + 0.5)) ;
		MS1000        = SAMPLE_RATE ;
		MS1500        = ((int) (1500/MS_PER_SAMPLE)) ;
		DERIV_LENGTH  = MS10 ;
		LPBUFFER_LGTH = ((int) (2*MS25)) ;
		HPBUFFER_LGTH = MS125 ;
		WINDOW_WIDTH  = MS80 ;
		}
		
	public static class PreBlankParameters 
		{
		public int PRE_BLANK ;
		/** filter delays plus pre blanking delay */
		public int FILTER_DELAY ;
		public int DER_DELAY ;
		
		public PreBlankParameters(QRSDetectorParameters qrsDetParas, int preBlank) 
			{
			PRE_BLANK = preBlank ;
			FILTER_DELAY = (int) (((double) qrsDetParas.DERIV_LENGTH/2) + ((double) qrsDetParas.LPBUFFER_LGTH/2 - 1) + (((double) qrsDetParas.HPBUFFER_LGTH-1)/2) + PRE_BLANK) ;
			DER_DELAY = qrsDetParas.WINDOW_WIDTH + FILTER_DELAY + qrsDetParas.MS100 ;
			}
		}
	}
