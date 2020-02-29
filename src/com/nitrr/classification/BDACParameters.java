
package com.nitrr.classification;


public class BDACParameters 
	{

	public int    BEAT_SAMPLE_RATE ;
	public double BEAT_MS_PER_SAMPLE ;

	public int    BEAT_MS10 ;
	public int    BEAT_MS20 ;
	public int    BEAT_MS40 ;
	public int    BEAT_MS50 ;
	public int    BEAT_MS60 ;
	public int    BEAT_MS70 ;
	public int    BEAT_MS80 ;
	public int    BEAT_MS90 ;
	public int    BEAT_MS100 ;
	public int    BEAT_MS110 ;
	public int    BEAT_MS130 ;
	public int    BEAT_MS140 ;
	public int    BEAT_MS150 ;
	public int    BEAT_MS250 ;
	public int    BEAT_MS280 ;
	public int    BEAT_MS300 ;
	public int    BEAT_MS350 ;
	public int    BEAT_MS400 ;
	public int    BEAT_MS1000 ;

	public int    BEATLGTH ;
	public int    MAXTYPES ;
	public int    FIDMARK ;
	
	public BDACParameters(int beatSampleRate) 
		{
		BEAT_SAMPLE_RATE   = beatSampleRate ;
		BEAT_MS_PER_SAMPLE = ( (double) 1000/ (double) BEAT_SAMPLE_RATE) ;

		BEAT_MS10          = ((int) (10/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS20          = ((int) (20/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS40          = ((int) (40/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS50          = ((int) (50/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS60          = ((int) (60/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS70          = ((int) (70/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS80          = ((int) (80/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS90          = ((int) (90/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS100         = ((int) (100/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS110         = ((int) (110/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS130         = ((int) (130/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS140         = ((int) (140/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS150         = ((int) (150/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS250         = ((int) (250/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS280         = ((int) (280/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS300         = ((int) (300/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS350         = ((int) (350/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS400         = ((int) (400/BEAT_MS_PER_SAMPLE + 0.5)) ;
		BEAT_MS1000        = BEAT_SAMPLE_RATE ;

		BEATLGTH           = BEAT_MS1000 ;
		MAXTYPES           = 8 ;
		FIDMARK            = BEAT_MS400 ;
		}
	}
