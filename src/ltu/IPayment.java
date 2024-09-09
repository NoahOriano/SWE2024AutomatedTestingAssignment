package ltu;

public interface IPayment
{
    /**
     * Will calculate the next available payment day.
     * 
     * @return next payment day (yyyymmdd)
     */
    public String getNextPaymentDay();

    /**
     * Will calculate monthly amount based on input parameters.
     * 
     * @param personId yyyymmdd-nnnn (birth id)
     * @param income yearly earned income in SEK
     * @param studyRate 0-100 (100 means full time studies)
     * @param completionRatio number of earned points / total points (0-100)
     * @return monthly paid amount
     * @throws IllegalArgumentException for invalid input arguments
     */
    public int getMonthlyAmount(String personId, int income, int studyRate, int completionRatio)
            throws IllegalArgumentException;

}
