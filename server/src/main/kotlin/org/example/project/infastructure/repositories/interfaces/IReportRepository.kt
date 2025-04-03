package org.example.project.infastructure.repositories.interfaces

import kotlinx.datetime.LocalDate
import org.example.project.model.Report

interface IReportRepository : IEntityRepository<Report, Int>