/*
 * This file is generated by jOOQ.
 */
package org.drakejin.carparazzi.entity.generated.tables;


import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Function;

import org.drakejin.carparazzi.entity.generated.Public;
import org.drakejin.carparazzi.entity.generated.tables.records.UserAnalysisStatsRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function9;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row9;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class UserAnalysisStats extends TableImpl<UserAnalysisStatsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.user_analysis_stats</code>
     */
    public static final UserAnalysisStats USER_ANALYSIS_STATS = new UserAnalysisStats();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserAnalysisStatsRecord> getRecordType() {
        return UserAnalysisStatsRecord.class;
    }

    /**
     * The column <code>public.user_analysis_stats.user_id</code>.
     */
    public final TableField<UserAnalysisStatsRecord, UUID> USER_ID = createField(DSL.name("user_id"), SQLDataType.UUID, this, "");

    /**
     * The column <code>public.user_analysis_stats.email</code>.
     */
    public final TableField<UserAnalysisStatsRecord, String> EMAIL = createField(DSL.name("email"), SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>public.user_analysis_stats.nickname</code>.
     */
    public final TableField<UserAnalysisStatsRecord, String> NICKNAME = createField(DSL.name("nickname"), SQLDataType.VARCHAR(100), this, "");

    /**
     * The column <code>public.user_analysis_stats.total_uploads</code>.
     */
    public final TableField<UserAnalysisStatsRecord, Long> TOTAL_UPLOADS = createField(DSL.name("total_uploads"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.user_analysis_stats.total_analyses</code>.
     */
    public final TableField<UserAnalysisStatsRecord, Long> TOTAL_ANALYSES = createField(DSL.name("total_analyses"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.user_analysis_stats.total_violations</code>.
     */
    public final TableField<UserAnalysisStatsRecord, Long> TOTAL_VIOLATIONS = createField(DSL.name("total_violations"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.user_analysis_stats.total_clips</code>.
     */
    public final TableField<UserAnalysisStatsRecord, Long> TOTAL_CLIPS = createField(DSL.name("total_clips"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.user_analysis_stats.total_downloads</code>.
     */
    public final TableField<UserAnalysisStatsRecord, Long> TOTAL_DOWNLOADS = createField(DSL.name("total_downloads"), SQLDataType.BIGINT, this, "");

    /**
     * The column <code>public.user_analysis_stats.last_upload_at</code>.
     */
    public final TableField<UserAnalysisStatsRecord, OffsetDateTime> LAST_UPLOAD_AT = createField(DSL.name("last_upload_at"), SQLDataType.TIMESTAMPWITHTIMEZONE(6), this, "");

    private UserAnalysisStats(Name alias, Table<UserAnalysisStatsRecord> aliased) {
        this(alias, aliased, null);
    }

    private UserAnalysisStats(Name alias, Table<UserAnalysisStatsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.view("""
        create view "user_analysis_stats" as  SELECT u.user_id,
          u.email,
          u.nickname,
          count(DISTINCT vu.upload_id) AS total_uploads,
          count(DISTINCT aj.job_id) AS total_analyses,
          count(DISTINCT ve.violation_id) AS total_violations,
          count(DISTINCT ec.clip_id) AS total_clips,
          count(DISTINCT dl.download_id) AS total_downloads,
          max(vu.uploaded_at) AS last_upload_at
         FROM (((((users u
           LEFT JOIN video_uploads vu ON ((u.user_id = vu.user_id)))
           LEFT JOIN analysis_jobs aj ON ((vu.upload_id = aj.upload_id)))
           LEFT JOIN violation_events ve ON ((aj.job_id = ve.job_id)))
           LEFT JOIN evidence_clips ec ON ((ve.violation_id = ec.violation_id)))
           LEFT JOIN download_logs dl ON ((u.user_id = dl.user_id)))
        GROUP BY u.user_id, u.email, u.nickname;
        """));
    }

    /**
     * Create an aliased <code>public.user_analysis_stats</code> table reference
     */
    public UserAnalysisStats(String alias) {
        this(DSL.name(alias), USER_ANALYSIS_STATS);
    }

    /**
     * Create an aliased <code>public.user_analysis_stats</code> table reference
     */
    public UserAnalysisStats(Name alias) {
        this(alias, USER_ANALYSIS_STATS);
    }

    /**
     * Create a <code>public.user_analysis_stats</code> table reference
     */
    public UserAnalysisStats() {
        this(DSL.name("user_analysis_stats"), null);
    }

    public <O extends Record> UserAnalysisStats(Table<O> child, ForeignKey<O, UserAnalysisStatsRecord> key) {
        super(child, key, USER_ANALYSIS_STATS);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UserAnalysisStats as(String alias) {
        return new UserAnalysisStats(DSL.name(alias), this);
    }

    @Override
    public UserAnalysisStats as(Name alias) {
        return new UserAnalysisStats(alias, this);
    }

    @Override
    public UserAnalysisStats as(Table<?> alias) {
        return new UserAnalysisStats(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public UserAnalysisStats rename(String name) {
        return new UserAnalysisStats(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserAnalysisStats rename(Name name) {
        return new UserAnalysisStats(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserAnalysisStats rename(Table<?> name) {
        return new UserAnalysisStats(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row9 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row9<UUID, String, String, Long, Long, Long, Long, Long, OffsetDateTime> fieldsRow() {
        return (Row9) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function9<? super UUID, ? super String, ? super String, ? super Long, ? super Long, ? super Long, ? super Long, ? super Long, ? super OffsetDateTime, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function9<? super UUID, ? super String, ? super String, ? super Long, ? super Long, ? super Long, ? super Long, ? super Long, ? super OffsetDateTime, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
