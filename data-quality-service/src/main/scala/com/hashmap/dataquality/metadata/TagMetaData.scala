package com.hashmap.dataquality.metadata

import com.fasterxml.jackson.annotation.JsonCreator

import scala.beans.BeanProperty

@JsonCreator
case class TagMetaData (@BeanProperty tag: String, @BeanProperty avgTagFrequency: String)