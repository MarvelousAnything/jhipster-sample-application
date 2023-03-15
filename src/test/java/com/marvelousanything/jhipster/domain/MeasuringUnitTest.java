package com.marvelousanything.jhipster.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.marvelousanything.jhipster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MeasuringUnitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MeasuringUnit.class);
        MeasuringUnit measuringUnit1 = new MeasuringUnit();
        measuringUnit1.setId(1L);
        MeasuringUnit measuringUnit2 = new MeasuringUnit();
        measuringUnit2.setId(measuringUnit1.getId());
        assertThat(measuringUnit1).isEqualTo(measuringUnit2);
        measuringUnit2.setId(2L);
        assertThat(measuringUnit1).isNotEqualTo(measuringUnit2);
        measuringUnit1.setId(null);
        assertThat(measuringUnit1).isNotEqualTo(measuringUnit2);
    }
}
