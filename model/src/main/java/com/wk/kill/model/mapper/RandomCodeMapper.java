package com.wk.kill.model.mapper;

import com.wk.kill.model.entity.RandomCode;

public interface RandomCodeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbggenerated Wed Sep 16 15:03:46 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbggenerated Wed Sep 16 15:03:46 CST 2020
     */
    int insert(RandomCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbggenerated Wed Sep 16 15:03:46 CST 2020
     */
    int insertSelective(RandomCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbggenerated Wed Sep 16 15:03:46 CST 2020
     */
    RandomCode selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbggenerated Wed Sep 16 15:03:46 CST 2020
     */
    int updateByPrimaryKeySelective(RandomCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbggenerated Wed Sep 16 15:03:46 CST 2020
     */
    int updateByPrimaryKey(RandomCode record);
}